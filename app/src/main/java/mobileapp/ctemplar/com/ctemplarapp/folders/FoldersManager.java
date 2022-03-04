package mobileapp.ctemplar.com.ctemplarapp.folders;

import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ALL_MAILS;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.ARCHIVE;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.CONTACT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.DRAFT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.INBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.OUTBOX;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SENT;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.SPAM;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.STARRED;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.TRASH;
import static mobileapp.ctemplar.com.ctemplarapp.repository.constant.MainFolderNames.UNREAD;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders.CustomFolderDTO;

public class FoldersManager {
    public static int getTitleResourceIdByName(String folder) {
        switch (folder) {
            case INBOX:
                return R.string.nav_drawer_inbox;
            case DRAFT:
                return R.string.nav_drawer_draft;
            case SENT:
                return R.string.nav_drawer_sent;
            case OUTBOX:
                return R.string.nav_drawer_outbox;
            case ALL_MAILS:
                return R.string.nav_drawer_all_mails;
            case UNREAD:
                return R.string.nav_drawer_unread;
            case STARRED:
                return R.string.nav_drawer_starred;
            case ARCHIVE:
                return R.string.nav_drawer_archive;
            case SPAM:
                return R.string.nav_drawer_spam;
            case CONTACT:
                return R.string.nav_drawer_contact;
            case TRASH:
                return R.string.nav_drawer_trash;
            default:
                return -1;
        }
    }

    public static int getResourceIdByName(@NonNull String name, List<CustomFolderDTO> customFolders) {
        int id;
        switch (name) {
            case INBOX:
                id = R.id.nav_inbox;
                break;
            case DRAFT:
                id = R.id.nav_draft;
                break;
            case SENT:
                id = R.id.nav_sent;
                break;
            case OUTBOX:
                id = R.id.nav_outbox;
                break;
            case ALL_MAILS:
                id = R.id.nav_all_mails;
                break;
            case UNREAD:
                id = R.id.nav_unread;
                break;
            case STARRED:
                id = R.id.nav_starred;
                break;
            case ARCHIVE:
                id = R.id.nav_archive;
                break;
            case SPAM:
                id = R.id.nav_spam;
                break;
            case CONTACT:
                id = R.id.nav_contact;
                break;
            case TRASH:
                id = R.id.nav_trash;
                break;
            default:
                id = -1;
        }
        if (id == -1) {
            for (CustomFolderDTO folder : customFolders) {
                if (name.equals(folder.getName())) {
                    return folder.getId();
                }
            }
        }
        return id;
    }

    @Nullable
    public static String getNameByResourceId(int id, @NonNull List<CustomFolderDTO> customFolders) {
        switch (id) {
            case R.id.nav_inbox:
                return INBOX;
            case R.id.nav_draft:
                return DRAFT;
            case R.id.nav_sent:
                return SENT;
            case R.id.nav_outbox:
                return OUTBOX;
            case R.id.nav_all_mails:
                return ALL_MAILS;
            case R.id.nav_starred:
                return STARRED;
            case R.id.nav_unread:
                return UNREAD;
            case R.id.nav_archive:
                return ARCHIVE;
            case R.id.nav_spam:
                return SPAM;
            case R.id.nav_trash:
                return TRASH;
            case R.id.nav_contact:
                return CONTACT;
            default:
                for (CustomFolderDTO folder : customFolders) {
                    if (id == folder.getId()) {
                        return folder.getName();
                    }
                }
        }
        return null;
    }
}
