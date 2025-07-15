package com.smf.concurrent;

/**
 * User TODO
 *
 * @author hf
 * @date 2024/1/4 11:04:36
 */
public class User {
    private String name;
    private int id;

    public User(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
