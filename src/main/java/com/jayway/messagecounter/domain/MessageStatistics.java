package com.jayway.messagecounter.domain;

public class MessageStatistics {

    private final long total;
    private final long numberOfLogs;
    private final long numberOfGames;
    private final long numberOfServices;

    public MessageStatistics(long total, long numberOfLogs, long numberOfGames, long numberOfServices) {
        this.total = total;
        this.numberOfLogs = numberOfLogs;
        this.numberOfGames = numberOfGames;
        this.numberOfServices = numberOfServices;
    }

    public long getTotal() {
        return total;
    }

    public long getNumberOfLogs() {
        return numberOfLogs;
    }

    public long getNumberOfGames() {
        return numberOfGames;
    }

    public long getNumberOfServices() {
        return numberOfServices;
    }

    public long getNumberOfUnknown() {
        return total - (numberOfLogs + numberOfGames + numberOfServices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageStatistics that = (MessageStatistics) o;

        if (numberOfGames != that.numberOfGames) return false;
        if (numberOfLogs != that.numberOfLogs) return false;
        if (numberOfServices != that.numberOfServices) return false;
        if (total != that.total) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (total ^ (total >>> 32));
        result = 31 * result + (int) (numberOfLogs ^ (numberOfLogs >>> 32));
        result = 31 * result + (int) (numberOfGames ^ (numberOfGames >>> 32));
        result = 31 * result + (int) (numberOfServices ^ (numberOfServices >>> 32));
        return result;
    }
}
