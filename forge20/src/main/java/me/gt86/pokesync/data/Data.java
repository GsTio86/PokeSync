package me.gt86.pokesync.data;

import net.william278.husksync.HuskSync;
import net.william278.husksync.data.UserDataHolder;
import org.jetbrains.annotations.NotNull;

public interface Data {

    void apply(@NotNull UserDataHolder user, @NotNull HuskSync plugin);

    interface PC extends Data {
        //To-DO
    }

    interface Party extends Data {
        //To-DO
    }

    interface Pokedex extends Data {
        //To-DO
    }

    interface Stats extends Data {
        //To-DO
    }

    interface Money extends Data {
        //To-DO
    }

    interface Daycare extends Data {
        //To-DO
    }

    interface MegaItem extends Data {
        //To-DO
    }

    interface Charm extends Data {
        //To-DO
    }

    interface Gift extends Data {
        //To-DO
    }

    interface TrainerCard extends Data {
        //To-DO
    }


    interface Cosmetic extends Data {
        //To-DO
    }

    interface Lure extends Data {
        //To-DO
    }

    interface Quest extends Data {
        //To-DO
    }

    interface Curry extends Data {
        //To-DO
    }

}
