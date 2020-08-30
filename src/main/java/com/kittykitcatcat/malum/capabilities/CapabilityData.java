package com.kittykitcatcat.malum.capabilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CapabilityData
{
    public void copyFrom(CapabilityData source)
    {
    }

    public void saveNBTData(CompoundNBT compound)
    {
        compound.putBoolean("husk", husk);
        compound.putBoolean("rogue", rogue);
        
    }
    public void loadNBTData(CompoundNBT compound)
    {
        husk = compound.getBoolean("husk");
        rogue = compound.getBoolean("rogue");
    }

    public LivingEntity cachedTarget;

    public LivingEntity getCachedTarget()
    {
        return cachedTarget;
    }

    boolean husk;
    
    public boolean getHusk()
    {
        return husk;
    }
    
    public boolean rogue;
    
    public boolean getRogue()
    {
        return rogue;
    }
    
}
