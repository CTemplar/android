package mobileapp.ctemplar.com.ctemplarapp.settings.invites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemInvitationCodeHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.invites.InviteCodeDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils;

public class InvitationCodesAdapter extends RecyclerView.Adapter<InvitationCodesAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;

    private List<InviteCodeDTO> inviteCodes = new ArrayList<>();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemInvitationCodeHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(inviteCodes.get(position));
    }

    @Override
    public int getItemCount() {
        return inviteCodes.size();
    }

    public void setItems(List<InviteCodeDTO> inviteCodes) {
        this.inviteCodes = inviteCodes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemInvitationCodeHolderBinding binding;

        public ViewHolder(ItemInvitationCodeHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(InviteCodeDTO inviteCode) {
            binding.expirationDateTextView.setText(DateUtils.simpleDate(inviteCode.getExpirationDate()));
            binding.usedTextView.setText(inviteCode.isUsed() ? R.string.yes : R.string.no);
            String code = inviteCode.getCode();
            binding.codeTextView.setText(code);
            binding.codeTextView.setOnClickListener(v -> AppUtils.setSystemClipboard(context, code));
        }
    }
}
