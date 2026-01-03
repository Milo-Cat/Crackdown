package net.spudacious5705.crackdown.lookup;

public abstract class SQLSearchResult {
    final int resultCount;
    public final long[] timestamp;

    protected SQLSearchResult(int resultCount, long[] timestamp) {
        this.resultCount = resultCount;
        this.timestamp = timestamp;
    }

    /**
     * only run on MAIN SERVER THREAD
     * ensure class also has a final Consumer<ChildClass> complete_action
     */
    public abstract void complete();
}
