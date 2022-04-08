package com.ctemplar.app.fdroid.settings.invites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.dto.DTOResource;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;
import com.ctemplar.app.fdroid.repository.dto.invites.InviteCodeDTO;

public class InvitationCodesViewModel extends ViewModel {
    private final UserRepository userRepository;

    public InvitationCodesViewModel() {
        userRepository = CTemplarApp.getUserRepository();
    }

    public MutableLiveData<DTOResource<InviteCodeDTO>> generateInviteCode() {
        return userRepository.generateInviteCode();
    }

    public MutableLiveData<DTOResource<PageableDTO<InviteCodeDTO>>> getInviteCodesLiveData() {
        return userRepository.getInviteCodesLiveData();
    }

    public void getInviteCodes(int limit, int offset) {
        userRepository.getInviteCodes(limit, offset);
    }
}
