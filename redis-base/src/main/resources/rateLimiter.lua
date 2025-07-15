--java 客户端送入三个参数
--limitkey redis中key的值
local key = KEYS[1]
--limit(次数)
local times = ARGV[1]
--expire(秒S)
local expre = ARGV[2]

--对key-value中的value+1操作 返回一个结果
local afterval = redis.call('incr', key);
if afterval == 1 then
    redis.call('expire', key, tonumber(expire))
    return 1;
end
if afterval > tonumber(times) then
    --限流了
    return 0;
end

return 1;