if (redis.call('exists',KEYS[1]) == 1) then
    if redis.call("get",KEYS[1]) == ARGV[1] then
        local lockRelease = redis.call("del",KEYS[1])
            if lockRelease==1 then
                return "1"
            end
                return "0"
    end
    return "-1"
end
return "1"