---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by root0day.
--- DateTime: 2023/2/2 16:23
---
local key = KEYS[1]
local count = tonumber(ARGV[1])
local time = tonumber(ARGV[2])
local current = redis.call('get', key)
if current and tonumber(current) > count then
    return tonumber(current)
end
current = redis.call('incr', key)
if tonumber(current) == 1 then
    redis.call('expire', key, time)
end
return tonumber(current)