package me.gt86.pokesync.hook;

import me.gt86.pokesyncmixins.api.event.PokeSyncEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class PokeSyncMixinsHook {

    public static void callPreEvent(UUID player) {
        MinecraftForge.EVENT_BUS.post(new PokeSyncEvent.Pre(player));
    }

    public static void callCompleteEvent(UUID player) {
        MinecraftForge.EVENT_BUS.post(new PokeSyncEvent.Complete(player));
    }

}
