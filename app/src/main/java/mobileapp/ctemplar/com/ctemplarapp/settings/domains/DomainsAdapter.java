package mobileapp.ctemplar.com.ctemplarapp.settings.domains;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.ItemDomainHolderBinding;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains.CustomDomainDTO;
import mobileapp.ctemplar.com.ctemplarapp.utils.LaunchUtils;

public class DomainsAdapter extends RecyclerView.Adapter<DomainsAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    private final String[] addresses;
    private List<CustomDomainDTO> items = new ArrayList<>();

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public DomainsAdapter(String[] addresses) {
        this.addresses = addresses;
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
            binding.catchAllEmailSpinner.setEnabled(dto.isCatchAll());

            List<String> domainAddresses = new ArrayList<>();
            for (String address : addresses) {
                if (address.contains(dto.getDomain())) {
                    domainAddresses.add(address);
                }
            }
            if (domainAddresses.isEmpty()) {
                binding.catchAllEmailSpinner.setVisibility(View.GONE);
            } else {
                SpinnerAdapter addressesAdapter = new ArrayAdapter<>(
                        context,
                        R.layout.item_domain_spinner,
                        domainAddresses
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
                listener.onCatchAll(dto.getId(), isChecked);
                binding.catchAllEmailSpinner.setEnabled(isChecked);
            });

            int selectionIndex = domainAddresses.indexOf(dto.getCatchAllEmail());
            binding.catchAllEmailSpinner.setSelection(selectionIndex == -1 ? 0 : selectionIndex,
                    false);
            binding.catchAllEmailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != -1) {
                        listener.onCatchAllEmail(dto.getId(), domainAddresses.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            binding.trashImageView.setOnClickListener(v -> listener.onDelete(dto.getId(), dto.getDomain()));
        }
    }

    public interface ItemClickListener {
        void onCatchAll(int domainId, boolean catchAll);

        void onCatchAllEmail(int domainId, String catchAllEmail);

        void onDelete(int domainId, String domainName);
    }
}
