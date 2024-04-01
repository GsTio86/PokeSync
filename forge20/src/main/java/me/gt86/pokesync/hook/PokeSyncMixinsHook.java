package me.gt86.pokesync.hook;

import me.gt86.pokesyncmixins.api.SyncLock;

import java.util.UUID;

public class PokeSyncMixinsHook {

    public boolean isSyncLocked(UUID player) {
        return SyncLock.isSyncLocked(player);
    }

    public void setSyncLock(UUID player, boolean lock) {
        SyncLock.setSyncLock(player, lock);
    }

    public void lock(UUID player) {
        SyncLock.lock(player);
    }

    public void unlock(UUID player) {
        SyncLock.unlock(player);
    }

}
