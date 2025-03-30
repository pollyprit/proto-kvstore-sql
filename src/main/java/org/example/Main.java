package org.example;

public class Main {
    public static void main(String[] args) {

        KVStore kvStore = new KVStore();

        kvStore.put("name1", "karan", 600);
        System.out.println(" name1 = " + kvStore.get("name1"));
        kvStore.del("name1");
        System.out.println(" name1 = " + kvStore.get("name1"));
    }
}