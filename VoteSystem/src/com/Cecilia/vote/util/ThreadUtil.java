package com.Cecilia.vote.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * 线程管理工具类
 * Created by Cecilia on 2017/8/5.
 */
public class ThreadUtil {

    private static ExecutorService exec;
    /**
     * 线程池管理工具
     * 传入一组带有返回值的线程，然后由工具类统一管理，并把最终的线程运行集合返回
     * @param list       线程集合
     * @param isBlock    是否阻塞标示位，如果为false，则线程启动后立即返回，否则等待所有线程均有结果后返回（推荐使用true）
     * @return           线程运行结果集合，仅返回已运行结束的线程集合，如都未结束，则返回尺寸为0的list
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static List<Object> runCheckCallable(List<Callable<Object>> list, boolean isBlock) throws InterruptedException, ExecutionException {
        //对参数进行检测
        if (CheckNull(list)) {
            return new ArrayList<>();
        }

        //初始化线程，打开一个线程池
        exec = Executors.newCachedThreadPool();
        //添加线程并返回Future线程
        List<Future<Object>> futureList = exec.invokeAll(list);

        if (!isBlock){
            return new ArrayList<>();
        }
        //检查并获取线程返回值
        return getAllCallableReturn(futureList);
    }

    /**
     * 对方法中传入的参数进行检测
     * @param list   传入的集合对象
     * @return       如果检测成功，返回true，反之，返回false
     */
    private static boolean CheckNull(List<Callable<Object>> list) {
        //检测list是否为空
        if (list==null||list.size()<1){
            return true;
        }
        //检测list中对象是否为空
        for (Callable callable :list){
            if (callable==null){
                return true;
            }
        }
        return false;
    }

    /**
     * 轮询获取所有Callable线程的返回值，直到所有返回值都被获取到（阻塞）
     * @param futureList     所有线程的管理类集合
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static List<Object> getAllCallableReturn(List<Future<Object>> futureList) throws ExecutionException, InterruptedException {
        List<Object> returnValue = new ArrayList<>(futureList.size());//为了使性能比较好，因此将ArrayList的长度为线程数量
        while(true){
            Iterator<Future<Object>> iterator = futureList.iterator();
            while(iterator.hasNext()){
                Future<Object> future = iterator.next();
                if (future.isDone()){
                    Object o = future.get();
                    returnValue.add(o);
                    iterator.remove();
                }
            }
            if (futureList.size()==0){
                break;
            }
            TimeUnit.MILLISECONDS.sleep(10*1000);
        }
        return returnValue;
    }

    /**
     * 线程池管理工具
     * 传入一组不带有返回值的线程，然后由工具类统一管理，对当前的线程进行监听
     * @param RunnableList       线程集合
     * @param isBlock            是否阻塞标示位，如果为false，则线程启动后立即返回，否则等待所有线程均有结果后返回（推荐使用true）
     * @return                   线程运行结果集合，仅返回已运行结束的线程集合，如都未结束，则返回尺寸为0的list
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void runCheckRunnable(List<Runnable> RunnableList,boolean isBlock) throws InterruptedException {
        //检测list是否为空
        if (RunnableList==null||RunnableList.size()<1){
            return;
        }
        //初始化线程池，使用一个线程池开启一个线程
        ExecutorService exec = Executors.newCachedThreadPool();
        //添加线程并返回Future线程
        List<Future> futureList = new ArrayList<>(RunnableList.size());
        //循环遍历，将每个线程依次添加进去
        for (Runnable runnable:RunnableList){
            if (runnable!=null){
                Future future = exec.submit(runnable);
                futureList.add(exec.submit(runnable));
            }
        }
        //线程不阻塞
        if (!isBlock){
            return;
        }
        while(true){
            Iterator<Future> iterator = futureList.iterator();
            while(iterator.hasNext()) {
                Future<Object> future = iterator.next();
                if (future.isDone()) {               //线程执行结束
                    iterator.remove();
                }
            }
            if (futureList.size()==0){
                break;
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    /**
     * 线程池管理工具
     * 传入一组不带有返回值的线程，然后由工具类统一管理，对当前的线程进行监听
     * @param RunnableList       线程集合
     * @return                   线程运行结果集合，仅返回已运行结束的线程集合，如都未结束，则返回尺寸为0的list
     */
    public static void runCheckRunnable(Runnable runnable){

        if (exec==null){
            exec = Executors.newCachedThreadPool();
        }
        exec.submit(runnable);
    }
}
