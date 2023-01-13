local key = KEYS[1]
local requestId = ARGV[1]
local ttl = ARGV[2]
if redis.call('setnx', key, requestId) == 1 then
    redis.call('expire', key, ttl)
    return 1
else
    local value = redis.call('get', key)
    if (value == requestId) then
        redis.call('expire', key, ttl)
        return 1
    end
end
return 0