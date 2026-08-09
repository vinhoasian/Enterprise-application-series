import java.util.concurrent.locks.ReentrantLock;

/*
 * Name: Vinh Vu
 * Class: Conveyor.java
 */
public class Conveyor {

    /** Subclass exposes the protected getOwner() method. */
    private static class ExposedLock extends ReentrantLock {
        public Thread getOwnerThread() {
            return super.getOwner();
        }
    }

    private final int         conveyorId;
    private final ExposedLock lock;

    public Conveyor(int conveyorId) {
        this.conveyorId = conveyorId;
        this.lock = new ExposedLock();
    }

    public int getConveyorId() {
        return conveyorId;
    }

    /** Attempts non-blocking tryLock. Returns true if acquired. */
    public boolean tryAcquire() {
        return lock.tryLock();
    }

    /** Releases the lock. */
    public void release() {
        lock.unlock();
    }

    /**
     * Returns the name of the thread currently holding this conveyor's lock,
     * or "unknown" if no thread holds it. Used for Output 8 diagnostics.
     */
    public String getOwnerName() {
        Thread t = lock.getOwnerThread();
        return (t != null) ? t.getName() : "unknown";
    }
}
