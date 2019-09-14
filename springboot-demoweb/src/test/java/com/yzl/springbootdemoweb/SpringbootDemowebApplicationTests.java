package com.yzl.springbootdemoweb;

import com.yzl.pojo.User;
import com.yzl.utils.BeanUtils;
import com.yzl.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringbootDemowebApplicationTests {

    @Resource
    private RedisUtil redisUtil;


    /*
    *   setnx 实现最简单的分布式锁
    * */
    @Test
    public void setlock() {
        DefaultRedisScript script = new DefaultRedisScript();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/deleteLua.lua")));
        script.setResultType(Long.class);

        String key ="product:001";
        String value = Thread.currentThread().getId()+"";
        try {
            boolean result = redisUtil.setnx(key,value,100);
            if(!result){
                System.out.println("系统繁忙中");
            } else {
                System.out.println("这里是你业务代码");
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Object result = redisUtil.execute(script,Collections.singletonList(key),value);
            System.out.println(result);
//            if (value.equals(redisUtil.get(key))){
//                redisUtil.del(key);
//
//            }
        }
    }


    /*
    *   hash实现存储对象
    * */
    @Test
    public void testHsetpojo(){
        User user = new User();
        user.setId(123);
        user.setAge(20);
        user.setAddr("北京");
        user.setName("yang");
        Map<String,Object> map = BeanUtils.beanToMap(user);
        String key = "user";
        redisUtil.hmset(key,map);
        System.out.println(redisUtil.hmget(key));
        System.out.println("id="+redisUtil.hget(key,"id"));

        String key2 = "user:"+user.getId();
        redisUtil.hmset(key2,map);
        System.out.println(redisUtil.hmget(key2));

    }

    /*
     *   hash实现购物车
     * */
    @Test
    public  void testcar(){
        String key ="carUser:123456";
        redisUtil.del(key);


        Map map = new HashMap();
        map.put("book:a11111",1);
        map.put("book:a11112",2);
        map.put("book:a11113",3);
        boolean b = redisUtil.hmset(key,map);
        System.out.println("key = "+redisUtil.hmget(key));

        //增加book:a11111的数量
        redisUtil.hincr(key,"book:a11111",1);
        System.out.println(redisUtil.hmget(key));
        //减少book:a11112的数量
        redisUtil.hincr(key,"book:a11112",-3);
        //或者redisUtil.hdecr(key,"book:a11111",1);
        System.out.println(redisUtil.hmget(key));
        //获取所有key1的field的值
        System.out.println("hegetall="+redisUtil.hmget(key));
        //获取key下面的map数量
        System.out.println("length="+redisUtil.hlen(key));
        //删除某个key下的map
        redisUtil.hdel(key,"book:a11112");
        System.out.println(redisUtil.hmget(key));
    }

    @Test
    public void testList(){
        String key = "a123456";
        redisUtil.del(key);
        String v1 = "aaaaa";
        String v2 = "bbbbb";
        String v3 = "ccccc";
        List list = new ArrayList();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        boolean b1 = redisUtil.lSet(key,list);
        System.out.println(redisUtil.lGet(key,0,-1));
        System.out.println(redisUtil.lGetIndex(key,0));

        System.out.println(redisUtil.lpop(key));
        System.out.println(redisUtil.rpop(key));
        System.out.println(redisUtil.lGet(key,0,-1));
        redisUtil.del(key);
        redisUtil.rpush(key,v1);
        System.out.println(redisUtil.lGet(key,0,-1));
        redisUtil.rpush(key,v2);
        System.out.println(redisUtil.lGet(key,0,-1));
        redisUtil.lpush(key,v3);
        System.out.println(redisUtil.lGet(key,0,-1));
    }

    @Test
    public void testVX(){
        String key = "VXuser:a123456";
        redisUtil.del(key);
        String message1 = "a1";
        String message2 = "b2";
        String message3 = "c3";

        //订阅号a发表了一片文章，文章id是a1
        redisUtil.lpush(key,message1);
        //订阅号b发表了一片文章，文章id是b2
        redisUtil.lpush(key,message2);
        //订阅号b发表了一片文章，文章id是c3
        redisUtil.lpush(key,message3);

        //用户获取
        System.out.println(redisUtil.lGet(key,0,-1));

    }

    @Test
    public void testset(){
        String key1 = "a1";
        redisUtil.del(key1);
        String key2 = "a2";
        redisUtil.del(key2);
        redisUtil.sSet(key1,1,2,3,4,5);
        System.out.println("key1="+redisUtil.sGet(key1));
        redisUtil.sSet(key2,1,2,5,6,7);
        System.out.println("key1="+redisUtil.sGet(key2));

        //获取key的数量
        System.out.println("length="+redisUtil.sGetSetSize(key1));
        //取key1和key2的交集
        System.out.println("交集="+redisUtil.sIntersect(key1,key2));
        //取key1和key2的差集
        System.out.println("差集="+redisUtil.sDifference(key1,key2));
        //取key1和key2的并集
        System.out.println("并集="+redisUtil.sUnion(key1,key2));

        //取key1的随机一个数
        System.out.println("随机数="+redisUtil.sRandom(key1));
        System.out.println("key1="+redisUtil.sGet(key1));
        //取key1的随机一个数，并且这个值在key中删除
        System.out.println("随机数="+redisUtil.spop(key1));
        System.out.println("key1="+redisUtil.sGet(key1));
    }

    @Test
    public void testSet2(){
        String key ="act:123456";
        redisUtil.del(key);
        long l = redisUtil.sSet(key,"a1","a2","a3","a4","a5");
        System.out.println(redisUtil.sGet(key));
        //抽奖
        System.out.println(redisUtil.spop(key));


        String user1 = "vxuser:a123456";
        String user2 = "vxuser:b123456";
        String user3 = "vxuser:c123456";
        String user4 = "vxuser:d123456";
        String user5 = "vxuser:e123456";
        String user6 = "vxuser:f123456";
        redisUtil.del("gzuser1");
        redisUtil.del("gzuser2");
        //user1关注user2,user3,user6
        redisUtil.sSet("gzuser1",user2,user3,user6);
        //user2关注user1,user3,user4,user5
        redisUtil.sSet("gzuser2",user1,user3,user4,user5);
        //共同好友
        System.out.println("共同好友"+redisUtil.sIntersect("gzuser1","gzuser2"));
        //你关注的好友也关注了他
        Set<String> set = redisUtil.sUnion(user1,user2);
        for (String s:set){
            redisUtil.sSet("bj",s);
        }
        System.out.println(redisUtil.sGet("bj"));
        System.out.println("你关注的好友也关注了他"+redisUtil.sDifference("bj","gzuser1"));
    }


    @Test
    public void testsingleRedisson(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379").setDatabase(0);
        RedissonClient redisson = Redisson.create(config);

        String key ="product:001";
        RLock lock = redisson.getLock(key);
        try {
            boolean res = lock.tryLock(10,100,TimeUnit.SECONDS);
            if (res){
                System.out.println("这里是你的业务代码");
            }else{
                System.out.println("系统繁忙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    @Test
    public void testsingleRedissonSync(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        RedissonClient redisson = Redisson.create(config);

        String key ="product:001";
        RLock lock = redisson.getLock(key);
        try {
            lock.lockAsync();
            lock.lockAsync(100,TimeUnit.SECONDS);
            Future<Boolean> res = lock.tryLockAsync(3,100, TimeUnit.SECONDS);
            if ( res.get()){
                System.out.println("这里是你的业务代码");
            }else{
                System.out.println("系统繁忙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    @Test
    public void testSentineRedisson(){
        Config config = new Config();
        config.useSentinelServers()
                .addSentinelAddress("redis://127.0.0.1:26379")
                .addSentinelAddress("redis://127.0.0.1:26389")
                .addSentinelAddress("redis://127.0.0.1:26399");
        RedissonClient redisson = Redisson.create(config);

        String key ="product:001";
        RLock lock = redisson.getLock(key);
        try {
            boolean res = lock.tryLock(10,100,TimeUnit.SECONDS);
            if (res){
                System.out.println("这里是你的业务代码");
            }else{
                System.out.println("系统繁忙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    @Test
    public void testMSRedisson(){
        Config config = new Config();
        config.useMasterSlaveServers()
                .setMasterAddress("redis://127.0.0.1:6379")
                .addSlaveAddress("redis://127.0.0.1:6380", "redis://127.0.0.1:6381");
        RedissonClient redisson = Redisson.create(config);

        String key ="product:001";
        RLock lock = redisson.getLock(key);
        try {
            boolean res = lock.tryLock(10,100,TimeUnit.SECONDS);
            if (res){
                System.out.println("这里是你的业务代码");
            }else{
                System.out.println("系统繁忙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    @Test
    public void testClusterRedisson(){
        Config config = new Config();
        config.useClusterServers()
                // 集群状态扫描间隔时间，单位是毫秒
                .setScanInterval(2000)
                //cluster方式至少6个节点(3主3从，3主做sharding，3从用来保证主宕机后可以高可用)
                .addNodeAddress("redis://127.0.0.1:6379" )
                .addNodeAddress("redis://127.0.0.1:6380")
                .addNodeAddress("redis://127.0.0.1:6381")
                .addNodeAddress("redis://127.0.0.1:6382")
                .addNodeAddress("redis://127.0.0.1:6383")
                .addNodeAddress("redis://127.0.0.1:6384");
        RedissonClient redisson = Redisson.create(config);

        String key ="product:001";
        RLock lock = redisson.getLock(key);
        try {
            boolean res = lock.tryLock(10,100,TimeUnit.SECONDS);
            if (res){
                System.out.println("这里是你的业务代码");
            }else{
                System.out.println("系统繁忙");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

}
