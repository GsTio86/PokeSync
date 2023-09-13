package me.gt86.pokesync.util;

import me.gt86.pokesync.config.Locales;
import me.gt86.pokesync.data.DataSnapshot;
import me.gt86.pokesync.player.CommandUser;
import me.gt86.pokesync.player.User;
import net.william278.paginedown.PaginatedList;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a chat-viewable paginated list of {@link DataSnapshot}s
 */
public class DataSnapshotList {

    // Used for displaying number ordering next to snapshots in the list
    private static final String[] CIRCLED_NUMBER_ICONS = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳".split("");

    @NotNull
    private final PaginatedList paginatedList;

    private DataSnapshotList(@NotNull List<DataSnapshot.Packed> snapshots, @NotNull User dataOwner,
                             @NotNull Locales locales) {
        final AtomicInteger snapshotNumber = new AtomicInteger(1);
        this.paginatedList = PaginatedList.of(snapshots.stream()
                .map(snapshot -> locales.getRawLocale("data_list_item",
                        getNumberIcon(snapshotNumber.getAndIncrement()),
                                        new SimpleDateFormat(locales.getRawLocale("time_format").orElse("MMM dd yyyy, HH:mm:ss.sss")).format(snapshot.getTimestamp()),
                        snapshot.getShortId(),
                        snapshot.getId().toString(),
                        snapshot.getSaveCause().getDisplayName(),
                        dataOwner.getUsername(),
                        snapshot.isPinned() ? "※" : "  ")
                    .orElse("• " + snapshot.getId())).toList(),
            locales.getBaseChatList(6)
                .setHeaderFormat(locales.getRawLocale("data_list_title", dataOwner.getUsername(),
                        "%first_item_on_page_index%", "%last_item_on_page_index%", "%total_items%")
                    .orElse(""))
                .setCommand("/pokesync:pmuserdata list " + dataOwner.getUsername())
                .build());
    }

    /**
     * Create a new {@link DataSnapshotList} from a list of {@link DataSnapshot}s
     *
     * @param snapshots The list of {@link DataSnapshot}s to display
     * @param user      The {@link User} who owns the {@link DataSnapshot}s
     * @param locales   The {@link Locales} instance
     * @return A new {@link DataSnapshotList}, to be viewed with {@link #displayPage(CommandUser, int)}
     */
    public static DataSnapshotList create(@NotNull List<DataSnapshot.Packed> snapshots, @NotNull User user,
                                          @NotNull Locales locales) {
        return new DataSnapshotList(snapshots, user, locales);
    }

    /**
     * Get an hasIcon for the given snapshot number, via {@link #CIRCLED_NUMBER_ICONS}
     *
     * @param number the snapshot number
     * @return the hasIcon for the given snapshot number
     */
    private static String getNumberIcon(int number) {
        if (number < 1 || number > 20) {
            return String.valueOf(number);
        }
        return CIRCLED_NUMBER_ICONS[number - 1];
    }

    /**
     * Display a page of the list of {@link DataSnapshot} to the user
     *
     * @param onlineUser The online user to display the message to
     * @param page       The page number to display
     */
    public void displayPage(@NotNull CommandUser onlineUser, int page) {
        onlineUser.sendMessage(paginatedList.getNearestValidPage(page));
    }

}
