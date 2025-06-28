package fr.jessee.blockQuota.util.iface;

public enum QuotaTable {
    BREAK("break_storage"),
    PLACE("place_storage");

    private final String tableName;

    QuotaTable(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
