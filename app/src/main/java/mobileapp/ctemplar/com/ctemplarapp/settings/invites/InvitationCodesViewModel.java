package mobileapp.ctemplar.com.ctemplarapp.settings.invites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserRepository;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.DTOResource;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.PageableDTO;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.invites.InviteCodeDTO;

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
