local setResult = redis.call("setnx", KEYS[1], ARGV[1])
if setResult == 1 then
    if tonumber(ARGV[2]) > 0 then
        local expireResult = redis.call("expire", KEYS[1], ARGV[2])
        if expireResult == 1 then
            return "1"
        end
        return "0"
    end
    return "1"
end
return "0"