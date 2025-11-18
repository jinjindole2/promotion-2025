# 일일 프로모션 서비스

## 1. 시스템 설계

![시스템구성도(프로모션 등록).png](image/시스템구성도(프로모션_등록).png)
![시스템구성도(프로모션 참여).png](image/시스템구성도(프로모션_참여).png)

대규모 프로모션 참여 및 조회에 중점으로 두고 설계했습니다. 특징은 아래와 같습니다.     

### 1) 비동기 논블록킹 API
- Kotlin Coroutine, R2DBC 등을 이용한 비동기 논블록킹 API로 많은 요청을 처리할 수 있습니다.

### 2) 레디스를 이용한 프로모션 참여
- 분산락을 이용해, 중복참여를 방지했습니다.
- 레디스에 참여횟수, 참여기간, 참여조건, 참여이력 등을 저장해, 레디스만으로 프로모션 참여를 처리할 수 있게 설계했습니다.
- 프로모션 참여를 Lua Script 를 이용해 원자적이고 빠르고 정확하게 처리했습니다.

```lua
-- ***** 프로모션 참여 Lua Script *****

-- 재고확인
local stock = tonumber(redis.call('get', KEYS[1]))
if not stock or stock == 0 then
    return -1
end

-- 참여조건 목록 조회
local joinTypeList = redis.call("SMEMBERS", KEYS[2])
for i = 1, #joinTypeList do

    -- 참여조건은 "프로모션ID:참여조건타입:값" 으로 이루어져있습니다. 이를 파싱해, 참여조건타입에 맞춰 값을 사용합니다.
    local item = joinTypeList[i]
    local col1 = string.find(item, ":")
    local col2 = string.find(item, ":", col1 + 1)
    if not col1 or not col2 then
        return -2
    end

    local joinType = string.sub(item, col1 + 1, col2 - 1)
    local value = string.sub(item, col2 + 1)

    -- 고객 참여횟수 제한
    if "N_DUP_LIMIT" == joinType then
        if redis.call("SCARD", KEYS[3]) == tonumber(value) then
            return -2
        end
    end

    -- 특정프로모션 참여이력 조회
    if "LEADING" == joinType then
        local key = "advUser:promoId:" .. value .. "userId" .. ARGV[3]
        if redis.call("SCARD", key) == 0 then
            return -2
        end
    end
end

-- 금일 참여이력 있는지 확인
if redis.call('SISMEMBER', KEYS[3], ARGV[1]) == 1 then
    return -3
end

-- 참여이력 저장
redis.call('SADD', KEYS[3], ARGV[1])

-- 재고 차감
redis.call('DECRBY', KEYS[1], 1)
return 0
```

### 3) 카프카를 이용한 비동기 포인트 적립 API 요청 
- 적립 API 응답과 무관하게, 프로모션 참여를 완료하기 위해 비동기로 처리했습니다. 
- 적립요청을 카프카로 전달하고, 리스너에서 적립 API 를 호출하도록 설계했습니다.

### 4) 배치(스케쥴러)를 이용한 레디스 - DB 동기화
- 레디스만으로 프로모션 참여가 가능하지만, DB 프로모션원장을 이용하는 서비스 (ex. 프로모션조회 API) 를 위해 동기화를 진행합니다.
- 트래픽이 대규모로 들어올것을 가정해, 카프카를 이용해 요청건마다 동기화가 아닌, 배치를 이용해 주기적으로 동기화를 진행했습니다. (30초 단위)

### + 추가 개선방안
- 현재는 기동에 용이하기 위해, API 내 카프카 리스너와 스케쥴러를 개발했습니다. 안정성을 위해 API, 리스너, 배치를 분리합니다.
- 현재 시스템에서 중요한 레디스, 카프카, DB에 대해 클러스터를 구성해 장애에 대응합니다.


```json
{
  "_id": ObjectId("..."),
  "id": "1",
  "title": "프로모션 A",
  "rewardAmount": 500,
  "leftJoinCount": 12,
  "limitJoinCount": 50,
  "content": "친구 초대하면 적립!",
  "imageUrl": "https://image.example.com/adv-a.jpg",
  "startDate": "2025-06-20",
  "endDate": "2025-06-30",
  "joinTypes": [
    {
      "joinType": "N_DUP_LIMIT",
      "limitDupJoinCount": 3
    },
    {
      "joinType": "LEADING",
      "leadPromoId": ["2", "3", "4"]
    }
  ],
  "createdDate": ISODate("2025-06-21T00:00:00Z"),
  "lastModifiedDate": ISODate("2025-06-21T01:00:00Z")
}
```