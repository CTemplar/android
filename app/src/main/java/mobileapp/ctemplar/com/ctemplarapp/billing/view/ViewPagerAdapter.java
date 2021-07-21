package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanInfo;
import mobileapp.ctemplar.com.ctemplarapp.databinding.SubscriptionLayoutBinding;


public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private final List<PlanInfo> items;
    private final ViewPagerAdapterListener listener;

    public ViewPagerAdapter(ViewPagerAdapterListener listener) {
        this.listener = listener;
        this.items = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(SubscriptionLayoutBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerAdapter.ViewHolder holder, int position) {
        holder.update(items.get(position));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setItems(List<PlanInfo> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public String getItemTitle(int position) {
        return this.items.get(position).getName();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SubscriptionLayoutBinding binding;

        public ViewHolder(SubscriptionLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void update(PlanInfo planInfo) {
            binding.subscribeButton.setOnClickListener(v -> listener.onSubscribeClicked(planInfo.getPlanType().getProductIdMonthly()));
            PlanData planData = planInfo.getPlanData();
            binding.sendingLimitsTextView.setText(context.getString(R.string.sending_limits_sub, planData.getMessagesPerDay()));
            binding.attachmentsLimitTextView.setText(context.getString(R.string.attachments_limit_sub, planData.getAttachmentUploadLimit()));
            binding.storageTextView.setText(context.getString(R.string.storage_sub, planData.getStorage()));
            binding.aliasesTextView.setText(context.getString(R.string.aliases_sub, planData.getAliases()));
            binding.customDomainsTextView.setText(context.getString(R.string.custom_domains_sub, planData.getCustomDomains()));
        }
    }

    public interface ViewPagerAdapterListener {
        void onSubscribeClicked(String sku);
    }

}
