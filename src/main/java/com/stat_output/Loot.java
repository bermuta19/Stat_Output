package com.stat_output;

public class Loot {

    private int id;
    private int quantity;
    private int price;

    public Loot(int id, int quantity)
    {
        this.id = id;
        this.quantity = quantity;
    }

    public void setId(int id){

        this.id = id;

    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public int getId(){
        return id;
    }
    public int getQuantity(){
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}
