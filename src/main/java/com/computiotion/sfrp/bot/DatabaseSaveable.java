package com.computiotion.sfrp.bot;

public interface DatabaseSaveable {
    void save();

    default void saveData() { save(); }
}
