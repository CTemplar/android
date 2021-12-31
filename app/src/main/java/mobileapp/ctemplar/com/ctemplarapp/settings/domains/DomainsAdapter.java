package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemDomainHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.LaunchUtils;

public class DomainsAdapter extends RecyclerView.Adapter<DomainsAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    private List<CustomDomainDTO> items = new ArrayList<>();

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDomainHolderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(items.get(position), position % 2 == 1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(CustomDomainDTO[] items) {
        this.items = new ArrayList<>(items.length);
        Collections.addAll(this.items, items);
        Collections.sort(this.items, (o1, o2) -> o1.getId() - o2.getId());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDomainHolderBinding binding;

        public ViewHolder(ItemDomainHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(CustomDomainDTO dto, boolean odd) {
            binding.domainValueTextView.setText(dto.getDomain());
            binding.verificationTextView.setActivated(dto.isDomainVerified());
            binding.mxTextView.setActivated(dto.isMxVerified());
            binding.spfTextView.setActivated(dto.isSpfVerified());
            binding.dkimTextView.setActivated(dto.isDkimVerified());
            binding.dmarcTextView.setActivated(dto.isDmarcVerified());
            binding.aliasesValueTextView.setText(String.valueOf(dto.getNumberOfAliases()));
            binding.usersValueTextView.setText(String.valueOf(dto.getNumberOfUsers()));
            binding.catchAllEmailCheckBox.setChecked(dto.isCatchAll());

            String catchAllEmail = dto.getCatchAllEmail();
            String[] catchAllEmailArray = dto.getCatchAllEmail() == null
                    ? new String[0]
                    : new String[]{dto.getCatchAllEmail()};
            if (TextUtils.isEmpty(catchAllEmail)) {
                binding.catchAllEmailSpinner.setVisibility(View.GONE);
            } else {
                SpinnerAdapter addressesAdapter = new ArrayAdapter<>(
                        context,
                        R.layout.item_domain_spinner,
                        catchAllEmailArray
                );
                binding.catchAllEmailSpinner.setAdapter(addressesAdapter);
            }

            binding.getRoot().setBackgroundColor(odd
                    ? context.getResources().getColor(R.color.colorDivider)
                    : context.getResources().getColor(R.color.colorPrimary));

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, DomainActivity.class);
                intent.putExtra(DomainActivity.EDIT_DOMAIN_KEY, dto.getId());
                LaunchUtils.launchActivity(context, intent);
            });

            binding.catchAllEmailCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int catchAllEmailPosition = binding.catchAllEmailSpinner.getSelectedItemPosition();
                if (catchAllEmailPosition == -1) {
                    return;
                }
                String catchAllEmailSelected = catchAllEmailArray[catchAllEmailPosition];
                listener.onCatchAllEmail(dto.getId(), isChecked, catchAllEmailSelected);
            });

            binding.catchAllEmailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    boolean catchAll = binding.catchAllEmailCheckBox.isChecked();
                    String catchAllEmailSelected = catchAllEmailArray[position];
                    listener.onCatchAllEmail(dto.getId(), catchAll, catchAllEmailSelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            binding.trashImageView.setOnClickListener(v -> listener.onDelete(dto.getId(), dto.getDomain()));
        }
    }

    public interface ItemClickListener {
        void onCatchAllEmail(int domainId, boolean checked, String email);
        void onDelete(int domainId, String domainName);
    }
}
