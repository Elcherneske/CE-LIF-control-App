package com.example.myapplicationforprojectversion1.model.model;

public class ByteCircleBuffer {
    private byte[] buff;
    private int head;
    private int tail;
    private int capacity = 16;





    public ByteCircleBuffer(int capacity){
        buff = new byte[capacity];
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
    }
    public ByteCircleBuffer()
    {
        buff = new byte[16];
        this.capacity = 16;
        this.head = 0;
        this.tail = 0;
    }
    public void push(byte[] in_buff, int size){
        int current_size = get_size();
        if(current_size + size > capacity - 1){//实现buff可拓展
            int new_capacity = capacity;
            int new_head = 0;
            int new_tail = 0;
            while(new_capacity - 1 < current_size + size){
                new_capacity = new_capacity<<1;
            }
            byte[] temp = pop_oversize(new_capacity);
            new_tail = current_size;

            this.buff = temp;
            this.head = new_head;
            this.tail = new_tail;
            this.capacity = new_capacity;
        }
        for(int i=0;i<size;i++){
            buff[tail] = in_buff[i];
            this.tail = (this.tail + 1)%this.capacity;
        }
    }

    public byte[] pop(int size){ //return的数组大小正好,要求数量超过原有数量会返回null
        if(size > get_size()) return null;

        byte[] result = new byte[size];

        for(int i=0;i<size;i++){
            result[i] = buff[this.head];
            this.head = (this.head + 1)%this.capacity;
        }
        return result;
    }

    public byte[] pop_oversize(int size){//return的数组就是size，多余空间不放东西
        byte[] result = new byte[size];
        int current_size = get_size();
        if(size > current_size){
            for(int i=0;i<current_size;i++){
                result[i] = buff[this.head];
                this.head = (this.head + 1)%this.capacity;
            }
        }
        else{
            for(int i=0;i<size;i++){
                result[i] = buff[this.head];
                this.head = (this.head + 1)%this.capacity;
            }
        }
        return result;
    }


    public boolean is_full()
    {
        if(((this.tail+1)%capacity) == this.head) return true;
        else return false;
    }

    public int get_size()
    {
        return (this.tail + this.capacity - this.head)% this.capacity;
    }





    //private function







}
