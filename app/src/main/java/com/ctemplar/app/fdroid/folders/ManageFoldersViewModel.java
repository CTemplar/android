package com.ctemplar.app.fdroid.folders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.ManageFoldersRepository;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;
import retrofit2.Response;

public class ManageFoldersViewModel extends ViewModel {
    private final ManageFoldersRepository manageFoldersRepository;

    public ManageFoldersViewModel() {
        manageFoldersRepository = CTemplarApp.getManageFoldersRepository();
    }

    public MutableLiveData<DTOResource<PageableDTO<CustomFolderDTO>>> getCustomFoldersLiveData() {
        return manageFoldersRepository.getCustomFoldersLiveData();
    }

    public void getCustomFolders(int limit, int offset) {
        manageFoldersRepository.getCustomFolders(limit, offset);
    }

    public MutableLiveData<DTOResource<CustomFolderDTO>> addFolder(String name, String color) {
        return manageFoldersRepository.addFolder(name, color);
    }

    public MutableLiveData<DTOResource<Response<Void>>> deleteFolder(long id) {
        return manageFoldersRepository.deleteFolder(id);
    }

    public MutableLiveData<DTOResource<CustomFolderDTO>> editFolder(long id, String name, String color) {
        return manageFoldersRepository.editFolder(id, name, color);
    }
}
