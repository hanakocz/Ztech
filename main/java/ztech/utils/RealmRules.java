package ztech.utils;

import java.io.Serializable;

public class RealmRules implements Serializable
{
    public boolean hardGregTechRecipe = true;
    public boolean bEnergy = false; // helper field, determines whether NetworkAnchor consumes energy
    public boolean wrenchRequired = true; // not sure wrenching should be here
    public float wrenchChance = 1.0F;
}