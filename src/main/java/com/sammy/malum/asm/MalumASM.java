package com.sammy.malum.asm;

import net.fabricmc.loader.api.FabricLoader;

public class MalumASM implements Runnable {

    @Override
    public void run() {
        extendEnums();
    }

    private static void extendEnums() {

    }

    /**
     * Remap a class name from intermediary to the runtime name
     *
     * @param intermediaryName the intermediary name for the class alone, such as 'class_123'
     * @return the fully qualified remapped name, such as 'net.minecraft.thing.Thing',
     * or the input with 'net.minecraft.' prepended if not found.
     */
    public static String mapC(String intermediaryName) {
        return FabricLoader.getInstance().getMappingResolver()
                .mapClassName("intermediary", "net.minecraft." + intermediaryName);
    }
}
