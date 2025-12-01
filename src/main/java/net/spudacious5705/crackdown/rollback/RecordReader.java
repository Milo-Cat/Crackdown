package net.spudacious5705.crackdown.rollback;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class RecordReader {


    static CompoundTag undo(String string){
        CompoundTag tag = null;
        try {
            tag = TagParser.parseTag("{foo:\"bar\",value:123}");
            System.out.println(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }


}
