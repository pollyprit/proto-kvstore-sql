package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        KVStore kvStore = new KVStore();

        kvStore.put("name1", "karan", 50);
        System.out.println(" name1 = " + kvStore.get("name1"));
        kvStore.del("name1");
        System.out.println(" name1 = " + kvStore.get("name1"));

        Thread.sleep(2 * 60 * 1000);
    }
}