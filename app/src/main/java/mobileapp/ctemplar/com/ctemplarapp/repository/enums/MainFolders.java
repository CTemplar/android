package mobileapp.ctemplar.com.ctemplarapp.repository.enums;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames;
import timber.log.Timber;

public enum MainFolders {
    INBOX(MainFolderNames.INBOX, R.string.nav_drawer_inbox, R.drawable.ic_drawer_inbox),
    DRAFT(MainFolderNames.DRAFT, R.string.nav_drawer_draft, R.drawable.ic_drawer_draft),
    SENT(MainFolderNames.SENT, R.string.nav_drawer_sent, R.drawable.ic_drawer_sent),
    OUTBOX(MainFolderNames.OUTBOX, R.string.nav_drawer_outbox, R.drawable.ic_drawer_outbox),
    ALL_MAILS(MainFolderNames.ALL_MAILS, R.string.nav_drawer_all_mails, R.drawable.ic_drawer_inbox),
    UNREAD(MainFolderNames.UNREAD, R.string.nav_drawer_unread, R.drawable.ic_drawer_inbox),
    STARRED(MainFolderNames.STARRED, R.string.nav_drawer_starred, R.drawable.ic_drawer_star),
    ARCHIVE(MainFolderNames.ARCHIVE, R.string.nav_drawer_archive, R.drawable.ic_drawer_archive),
    SPAM(MainFolderNames.SPAM, R.string.nav_drawer_spam, R.drawable.ic_drawer_spam),
    TRASH(MainFolderNames.TRASH, R.string.nav_drawer_trash, R.drawable.ic_drawer_trash);

    private final String name;
    private final int displayNameResourceId;
    private final int drawableResourceId;

    MainFolders(String name, int displayNameResourceId, int drawableResourceId) {
        this.name = name;
        this.displayNameResourceId = displayNameResourceId;
        this.drawableResourceId = drawableResourceId;
    }

    public String getName() {
        return name;
    }

    public int getDisplayNameResourceId() {
        return displayNameResourceId;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public static MainFolders get(String name) {
        if (name == null) {
            Timber.e("Folder is null");
        }
        for (MainFolders folder : MainFolders.values()) {
            if (folder.name.equals(name)) {
                return folder;
            }
        }
        Timber.w("%s folder not found", name);
        return MainFolders.INBOX;
    }
}
