package net.spudacious5705.crackdown.lookup;

public abstract class SQLSearchResult {
    protected final int resultCount;

    protected SQLSearchResult(int resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * only run on MAIN SERVER THREAD
     * ensure class also has a final Consumer<ChildClass> complete_action
     */
    public abstract void complete();
}
