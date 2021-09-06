package mobileapp.ctemplar.com.ctemplarapp.billing.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.SkuDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.billing.BillingConstants;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.CurrentPlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanData;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanInfo;
import mobileapp.ctemplar.com.ctemplarapp.billing.model.PlanType;
import mobileapp.ctemplar.com.ctemplarapp.databinding.SubscriptionLayoutBinding;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private final List<PlanInfo> items;
    private final ViewPagerAdapterListener listener;
    private boolean isYearlyPlanCycle = true;
    private CurrentPlanData currentPlanData;

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

    public void setCurrentPlanData(CurrentPlanData currentPlanData) {
        this.currentPlanData = currentPlanData;
        notifyDataSetChanged();
    }

    public String getItemTitle(int position) {
        return this.items.get(position).getName();
    }

    public int getItemIndexByPlanType(PlanType planType) {
        int counter = 0;
        for (PlanInfo item : items) {
            if (item.getPlanType() == planType) {
                return counter;
            }
            ++counter;
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SubscriptionLayoutBinding binding;

        public ViewHolder(SubscriptionLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void update(PlanInfo planInfo) {
            PlanData planData = planInfo.getPlanData();
            binding.subscribeButton.setOnClickListener(v -> listener.onSubscribeClicked(
                    isYearlyPlanCycle
                            ? planInfo.getPlanType().getProductIdAnnual()
                            : planInfo.getPlanType().getProductIdMonthly()));
            SkuDetails skuDetails = isYearlyPlanCycle ? planInfo.getYearlyPlanSkuDetails() : planInfo.getMonthlyPlanSkuDetails();
            if (skuDetails == null) {
                if (planInfo.getPlanType() == PlanType.FREE) {
                    binding.subscribeButton.setText(PlanType.FREE.name());
                    binding.subscribeButton.setEnabled(true);
                } else {
                    binding.subscribeButton.setText(R.string.not_available_on_android);
                    binding.subscribeButton.setEnabled(false);
                }
            } else {
                binding.subscribeButton.setText(context.getString(R.string.upgrade_button, skuDetails.getPrice()));
                binding.subscribeButton.setEnabled(true);
            }
            if (currentPlanData != null) {
                if (currentPlanData.getPlanType() == planInfo.getPlanType()
                        && (planInfo.getPlanType() == PlanType.FREE
                        || isYearlyPlanCycle == !currentPlanData.getPaymentTransactionDTO()
                        .getPaymentType().equals(BillingConstants.MONTHLY))
                ) {
                    binding.subscribeButton.setText(R.string.current_plan);
                    binding.subscribeButton.setEnabled(true);
                    binding.subscribeButton.setOnClickListener(v ->
                            listener.onOpenCurrentPlanClicked(currentPlanData));
                }
            }
            String messagesPerDay = planData.getMessagesPerDay();
            if (EditTextUtils.isNumeric(messagesPerDay)) {
                binding.sendingLimitsValueTextView.setText(context.getString(R.string.sending_limits_value, messagesPerDay));
            } else {
                binding.sendingLimitsValueTextView.setText(messagesPerDay);
            }
            binding.attachmentsLimitValueTextView.setText(context.getString(R.string.attachments_limit_value, planData.getAttachmentUploadLimit()));
            binding.storageValueTextView.setText(context.getString(R.string.storage_value, planData.getStorage()));
            binding.aliasesValueTextView.setText(String.valueOf(planData.getAliases()));
            binding.customDomainsValueTextView.setText(String.valueOf(planData.getCustomDomains()));

            binding.encryptionInTransit.setSelected(planData.isEncryptionInTransit());
            binding.encryptionAtRest.setSelected(planData.isEncryptionAtRest());
            binding.encryptedAttachments.setSelected(planData.isEncryptedAttachments());
            binding.encryptedSubject.setSelected(planData.isEncryptedSubjects());
            binding.encryptedBody.setSelected(planData.isEncryptedBody());
            binding.virusProtection.setSelected(planData.isVirusDetectionTool());
            binding.security2fa.setSelected(planData.isTwoFa());
            binding.antiPhishingPhrase.setSelected(planData.isAntiPhishing());
            binding.bruteForceProof.setSelected(planData.isBruteForceProtection());
            binding.zeroKnowledgePrivacy.setSelected(planData.isZeroKnowledgePassword());
            binding.anonymizedIp.setSelected(planData.isAnonymizedIp());
            binding.selfDestructingEmails.setSelected(planData.isSelfDestructingEmails());
            binding.deadManTimer.setSelected(planData.isDeadManTimer());
            binding.delayedDelivery.setSelected(planData.isDelayedDelivery());
            binding.catchAllDomains.setSelected(planData.isCatchAllEmail());
            binding.remoteEncryptedLink.setSelected(planData.isRemoteEncryptedLink());
            binding.exclusiveBetaAccess.setSelected(planData.isExclusiveAccess());
        }
    }

    public void setPlanCycle(boolean isYearly) {
        isYearlyPlanCycle = isYearly;
        notifyDataSetChanged();
    }

    public interface ViewPagerAdapterListener {
        void onSubscribeClicked(String sku);

        void onOpenCurrentPlanClicked(CurrentPlanData currentPlanData);
    }
}
