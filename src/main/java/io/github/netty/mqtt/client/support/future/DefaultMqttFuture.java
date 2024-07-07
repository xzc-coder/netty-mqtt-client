package io.github.netty.mqtt.client.support.future;


import io.github.netty.mqtt.client.support.util.LogUtils;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认的MqttFuture实现
 * @author: xzc-coder
 */
public class DefaultMqttFuture<T> extends MqttFuture<T> {

    /**
     * 成功无返回值标志
     */
    private static final Object SUCCESS = new Object();

    /**
     * 被阻塞的线程
     */
    private final Set<Thread> waitThreads = ConcurrentHashMap.newKeySet();

    /**
     * 结果
     */
    private final AtomicReference<Object> result = new AtomicReference<>();

    /**
     * 锁
     */
    private final Lock lock = new ReentrantLock();

    /**
     * 条件
     */
    private final Condition condition = this.lock.newCondition();

    public DefaultMqttFuture(MqttFutureKey mqttFutureKey) {
        super(mqttFutureKey);
    }

    public DefaultMqttFuture(MqttFutureKey mqttFutureKey, Object parameter) {
        super(mqttFutureKey, parameter);
    }

    public DefaultMqttFuture(String clientId, Object key) {
        super(clientId, key);
    }


    public DefaultMqttFuture(String clientId, Object key, Object parameter) {
        super(clientId, key, parameter);
    }

    @Override
    public void awaitComplete() throws InterruptedException {
        this.doAwaitCompleteResult(null, true);
    }

    @Override
    public boolean awaitComplete(long timeout) throws InterruptedException {
        return this.doAwaitCompleteResult(timeout, true);
    }

    @Override
    public boolean awaitCompleteUninterruptibly() {
        try {
            return this.doAwaitCompleteResult(null, false);
        } catch (InterruptedException e) {
            //应该不存在这种情况
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitCompleteUninterruptibly(long timeout) {
        try {
            return doAwaitCompleteResult(timeout, false);
        } catch (InterruptedException e) {
            //应该不存在这种情况
            throw new InternalError();
        }
    }

    private boolean doAwaitCompleteResult(Long timeout, boolean interruptable) throws InterruptedException {
        this.doAwaitComplete(timeout, interruptable);
        return this.isDone();
    }

    private void doSyncResult(Long timeout,boolean interruptable) throws InterruptedException, TimeoutException {
        this.doAwaitComplete(timeout, interruptable);
        if(!this.isDone()) {
            throw new TimeoutException();
        }else {
            if (!this.isSuccess()) {
                rethrowIfFailed();
            }
        }
    }

    /**
     * 等待完成
     *
     * @param timeout       超时时间
     * @param interruptable 是否可打断
     * @throws InterruptedException 打断异常
     */
    private void doAwaitComplete(Long timeout, boolean interruptable) throws InterruptedException {
        //如果已经完成，则直接结束
        if (this.isDone()) {
            return;
        }
        //设置超时时间
        Long deadline = null;
        if (timeout != null) {
            if (timeout <= 0) {
                return;
            }
            deadline = System.currentTimeMillis() + timeout;
        }
        //可以被打断，并且进来之前已经被打断 则抛出异常
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException();
        }

        //中间过程有没有被打断
        boolean interrupted = false;
        this.lock.lock();
        try {
            while (!this.isDone()) {
                try {
                    //阻塞等待
                    this.waitThreads.add(Thread.currentThread());
                    if (deadline != null) {
                        //超时等待
                        this.condition.awaitUntil(new Date(deadline));
                        //如果到了超时时间，则结束
                        if (System.currentTimeMillis() >= deadline) {
                            break;
                        }
                    } else {
                        //等待
                        this.condition.await();
                    }
                } catch (InterruptedException interruptedException) {
                    //如果允许被打断，则抛出异常
                    if (interruptable) {
                        throw interruptedException;
                    } else {
                        //不允许被打断 则标记中间过程被打断
                        interrupted = true;
                    }
                } finally {
                    //移除当前线程等待
                    this.removeWaiter(Thread.currentThread());
                }
            }
            //如果不允许被打断并且中间过程被打断，则重新打断一次
            if (!interruptable && interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 移除一个等待
     *
     * @param currentThread 线程
     */
    private void removeWaiter(Thread currentThread) {
        this.waitThreads.remove(currentThread);
    }


    @Override
    protected boolean doSetSuccess(T result) {
        Object value = (result == null) ? SUCCESS : result;
        return this.setValue(value);
    }

    @Override
    public T getResult() {
        return getRealResult();
    }

    @Override
    public MqttFuture sync() throws InterruptedException, TimeoutException {
        this.doSyncResult(null,true);
        return this;
    }

    @Override
    public MqttFuture sync(long timeout) throws InterruptedException, TimeoutException {
        this.doSyncResult(timeout,true);
        return this;
    }

    @Override
    public MqttFuture syncUninterruptibly() {
        try {
            this.doSyncResult(null,false);
        } catch (InterruptedException | TimeoutException e) {
            //忽略 应该不存在这种情况
        }
        return this;
    }

    @Override
    public MqttFuture syncUninterruptibly(long timeout) throws TimeoutException {
        try {
            this.doSyncResult(timeout,false);
        } catch (InterruptedException e) {
            //忽略 应该不存在这种情况
        }
        return this;
    }

    @Override
    protected boolean doSetFailure(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("this Throwable cause is null");
        }
        CauseHolder causeHolder = new CauseHolder(cause);
        return this.setValue(causeHolder);
    }

    @Override
    public boolean isDone() {
        return this.result.get() != null;
    }

    @Override
    public boolean isSuccess() {
        return this.result.get() != null && !(this.result.get() instanceof CauseHolder);
    }

    @Override
    public Throwable getCause() {
        Throwable cause = null;
        if (this.result.get() != null && this.result.get() instanceof CauseHolder) {
            CauseHolder causeHolder = (CauseHolder) this.result.get();
            cause = causeHolder.cause;
        }
        return cause;
    }

    @Override
    public void addListener(MqttFutureListener<T> listener) {
        this.listeners.add(listener);
        if (this.isDone()) {
            notifyListener(listener);
        }
    }

    /**
     * 唤醒监听器
     *
     * @param listener 监听器
     */
    private void notifyListener(MqttFutureListener<T> listener) {
        try {
            boolean success = this.removeListener(listener);
            if (success) {
                listener.operationComplete(this);
            }
        } catch (Throwable throwable) {
            LogUtils.error(DefaultMqttFuture.class,"exception occurred in listener call,cause: " + throwable.getMessage());
        }
    }

    @Override
    public boolean removeListener(MqttFutureListener<T> listener) {
        return this.listeners.remove(listener);
    }

    @Override
    public boolean cancel() throws Exception {
        CauseHolder causeHolder = new CauseHolder(new CancellationException());
        return setValue(causeHolder);
    }

    @Override
    public boolean isCancelled() {
        return result.get() instanceof CauseHolder && ((CauseHolder) this.result.get()).cause instanceof CancellationException;
    }

    /**
     * 设值
     *
     * @param value 值
     * @return 是否成功
     */
    private boolean setValue(Object value) {
        //cas设值成功
        if (this.result.compareAndSet(null, value)) {
            notifyAllThreadAndListener();
            return true;
        }
        return false;
    }

    /**
     * 唤醒所有的等待线程
     */
    private void notifyAllThreadAndListener() {
        this.lock.lock();
        try {
            if (this.waitThreads.size() > 0) {
                this.condition.signalAll();
            }
        } finally {
            this.lock.unlock();
        }

        for (MqttFutureListener<T> listener : this.listeners) {
            this.notifyListener(listener);
        }
    }

    /**
     * 拿到真实的值
     *
     * @return 值
     */
    private T getRealResult() {
        //如果是成功的空值，则返回null
        if (this.result.get() == SUCCESS) {
            return null;
        }
        return (T) this.result.get();
    }


    /**
     * 异常原因
     */
    private static final class CauseHolder {
        /**
         * 原因
         */
        private final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

}
