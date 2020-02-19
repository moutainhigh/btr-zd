package com.baturu.zd.service.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qipan
 */
@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_CODE = "utf-8";

    /**
     * 获取指定列表的范围数据
     *
     * @param key   　列表名
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    public List<String> lrange(final String key, final int start, final int end) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            List<String> result = new ArrayList<>();

            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                List<byte[]> byteList = connection.lRange(key.getBytes(), start, end);
                for (byte[] b : byteList) {
                    try {
                        result.add(new String(b, REDIS_CODE));
                    } catch (UnsupportedEncodingException e) {
                        log.error("fail to parse byte[] to string", e);
                        break;
                    }
                }
                return result;
            }
        });
    }

    /**
     * 从队列的左边取出一条数据
     *
     * @param key 列表名
     * @return
     */
    public String lpop(final String key) {
        return redisTemplate.execute((RedisCallback<String>) (connection) -> {
            byte[] result = connection.lPop(key.getBytes());
            if (result != null) {
                try {
                    return new String(result, REDIS_CODE);
                } catch (UnsupportedEncodingException e) {
                    log.error("fail to parse byte[] to string", e);
                }
            }
            return null;
        });
    }

    /**
     * 从列表右边添加数据
     *
     * @param key    列表名
     * @param values 数据
     * @return
     */
    public long rpush(String key, String... values) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            long result = 0;
            for (String v : values) {
                connection.rPush(key.getBytes(), v.getBytes());
                result++;
            }
            return result;
        });
    }

    /**
     * 从列表右边添加数据,并且设置列表的存活时间
     *
     * @param key      列表名
     * @param liveTime 存活时间(单位 秒)
     * @param values   数据
     * @return
     */
    public long rpush(final String key, final int liveTime, final String... values) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            long result = 0;
            for (String v : values) {
                connection.rPush(key.getBytes(), v.getBytes());
                result++;
            }
            if (liveTime > 0) {
                connection.expire(key.getBytes(), liveTime);
            }
            return result;
        });
    }

    /**
     * 从队列的右边取出一条数据
     *
     * @param key 列表名
     * @return
     */
    public String rpop(final String key) {
        return redisTemplate.execute((RedisCallback<String>) (connection) -> {
            byte[] result = connection.rPop(key.getBytes());
            if (result != null) {
                try {
                    return new String(result, REDIS_CODE);
                } catch (UnsupportedEncodingException e) {
                    log.error("fail to parse byte[] to string", e);
                }
            }
            return null;
        });
    }

    /**
     * 返回列表的长度
     *
     * @param key
     * @return
     */
    public long llen(final String key) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            Long l = connection.lLen(key.getBytes());
            return l == null ? 0 : l;
        });
    }

    /**
     * @param keys
     * @return
     */
    public long del(final String... keys) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            long result = 0;
            for (String k : keys) {
                result = connection.del(k.getBytes());
            }
            return result;
        });
    }

    /**
     * 添加key value 并且设置存活时间(byte)
     *
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            connection.set(key, value);
            if (liveTime > 0) {
                connection.expire(key, liveTime);
            }
            return 1L;
        });

    }

    /**
     * 添加key value 并且设置存活时间
     *
     * @param key
     * @param value
     * @param liveTime 单位秒
     */
    public void set(String key, String value, long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);

    }

    /**
     * 添加key value
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    /**
     * 获取redis value (String)
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return redisTemplate.execute((RedisCallback<String>) (connection) -> {
            byte[] result = connection.get(key.getBytes());
            if (result != null) {
                try {
                    return new String(result, REDIS_CODE);
                } catch (UnsupportedEncodingException e) {
                    log.error("fail to parse byte[] to string", e);
                }
            }
            return null;
        });
    }

    /**
     * 如果key不存在添加key value 并且设置存活时间(byte)，当key已经存在时，就不做任何操作
     *
     * @param key
     * @param value
     * @param liveTime
     */
    public long setnx(final byte[] key, final byte[] value, final long liveTime) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            long result = 0L;
            boolean isSuccess = connection.setNX(key, value);
            if (isSuccess) {
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                result = 1L;
            }
            return result;
        });
    }

    /**
     * 如果key不存在添加key value 并且设置存活时间，当key已经存在时，就不做任何操作
     *
     * @param key
     * @param value
     * @param liveTime 单位秒
     */
    public long setnx(String key, String value, long liveTime) {
        return this.setnx(key.getBytes(), value.getBytes(), liveTime);

    }

    /**
     * 如果key不存在添加key value，当key已经存在时，就不做任何操作
     *
     * @param key
     * @param value
     */
    public long setnx(String key, String value) {
        return this.setnx(key, value, 0L);
    }

    /**
     * 如果key不存在添加key value (字节)(序列化)，当key已经存在时，就不做任何操作
     *
     * @param key
     * @param value
     */
    public long setnx(byte[] key, byte[] value) {
        return this.setnx(key, value, 0L);

    }

    /**
     * 检查key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.execute((RedisCallback<Boolean>) (connection) -> connection.exists(key.getBytes()));
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param seconds 单位(秒)
     * @return
     */
    public boolean expire(final String key, final long seconds) {
        return redisTemplate.execute((RedisCallback<Boolean>) (connection) -> connection.expire(key.getBytes(), seconds));
    }

    /**
     * @param key
     * @return
     */
    public long incr(final String key) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.incr(key.getBytes()));
    }

    /**
     * @param key
     * @param len
     * @return
     */
    public long incrBy(final String key, final long len) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.incrBy(key.getBytes(), len));
    }

    /**
     * @param key
     * @param len
     * @return
     */
    public double incrBy(final String key, final double len) {
        return redisTemplate.execute((RedisCallback<Double>) (connection) -> connection.incrBy(key.getBytes(), len));
    }

    /**
     * 往hash里面设一个值
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(final String key, final String field, final String value) {
        redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            connection.hSet(key.getBytes(), field.getBytes(), value.getBytes());
            return 1L;
        });
    }

    /**
     * 往hash里面设一个值
     *
     * @param key
     * @param field
     * @param value
     */
    public boolean hsetnx(final String key, final String field, final String value) {
        return redisTemplate.execute((RedisCallback<Boolean>) (connection) -> {
            Boolean ret = connection.hSetNX(key.getBytes(), field.getBytes(), value.getBytes());
            return ret != null && ret;
        });
    }

    /**
     * 从hash里读取一个值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(final String key, final String field) {
        return redisTemplate.execute((RedisCallback<String>) (connection) -> {
            byte[] b = connection.hGet(key.getBytes(), field.getBytes());
            if (b != null) {
                try {
                    return new String(b, REDIS_CODE);
                } catch (UnsupportedEncodingException e) {
                    log.error("fail to parse byte[] to string", e);
                }
            }
            return null;
        });
    }

    /**
     * 判断hash内是否存在指定field
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(final String key, final String field) {
        return redisTemplate.execute((RedisCallback<Boolean>) (connection) -> {
            Boolean b = connection.hExists(key.getBytes(), field.getBytes());
            return b != null && b;
        });
    }

    /**
     * 删除hash内的一个field
     *
     * @param key
     * @param field
     * @return
     */
    public long hdel(final String key, final String field) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            Long l = connection.hDel(key.getBytes(), field.getBytes());
            return l == null ? 0 : l;
        });
    }

    /**
     * 原子增长
     *
     * @param key
     * @param field
     * @param d
     * @return
     */
    public long hincrBy(final String key, final String field, final long d) {
        return redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            Long l = connection.hIncrBy(key.getBytes(), field.getBytes(), d);
            return l == null ? 0 : l;
        });
    }
}
