package com.protovate.verity.ui.adapters;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protovate.verity.App;
import com.protovate.verity.R;
import com.protovate.verity.data.Notification;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.responses.Invitations;
import com.protovate.verity.service.ApiClient;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 7/31/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;

    private ArrayList<Notification> mData = new ArrayList<>();

    public NotificationsAdapter(Application application) {
        ((App) application).component().inject(this);
        getData();
    }

    void getData() {
        String pendingHeader = "Pending Request";
        String historyHeader = "History";

        Observable.zip(
                apiClient.getPendingInvitations(profile.getAccessToken()).subscribeOn(Schedulers.io()),
                apiClient.getHistoryInvitations(profile.getAccessToken()).subscribeOn(Schedulers.io()),
                (pending, history) -> new ArrayList<>(Arrays.asList((Invitations.Data.Item[]) ArrayUtils
                        .addAll(pending.getData().getItems(), history.getData().getItems()))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        items -> {
                            mData = new ArrayList<>();
                            String previousHeader = "";
                            for (Invitations.Data.Item item : items) {
                                if (item.getStatusId() == 2) { // pending = 2
                                    if (!previousHeader.equals(pendingHeader)) {
                                        mData.add(new Notification(new Invitations().new Data().new Item(pendingHeader), true));
                                        previousHeader = pendingHeader;
                                    }
                                }
                                if (item.getStatusId() == 1 || item.getStatusId() == 0) { // accepted = 1, declined = 0
                                    if (!previousHeader.equals(historyHeader)) {
                                        mData.add(new Notification(new Invitations().new Data().new Item(historyHeader), true));
                                        previousHeader = historyHeader;
                                    }
                                }
                                mData.add(new Notification(item, false));
                            }
                            notifyDataSetChanged();
                        }, error -> System.out.println("error = " + error)
                );


    }

    @Override public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        }

        return new NotificationViewHolder(view);
    }

    @Override public void onBindViewHolder(NotificationViewHolder holder, int position) {
        final Notification item = mData.get(position);

        if (item.isHeader()) {
            holder.mTextView.setText(item.getItem().text);
        } else {

            if (!TextUtils.isEmpty(item.getItem().getInviter().getCompanyName())) {
                holder.mTextView.setText(item.getItem().getInviter().getCompanyName() + " would like to add you as a provider");
            } else {
                holder.mTextView.setText(item.getItem().getInviter().getFirstName() + " " +
                        item.getItem().getInviter().getLastName() + " would like to add you as a provider");
            }

            holder.mBtnAccept.setOnClickListener(
                    view -> apiClient.acceptInvitation(String.valueOf(item.getItem().getId()), profile.getAccessToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    invitation -> getData(),
                                    error -> System.out.println("error = " + error)
                            )
            );

            holder.mBtnDecline.setOnClickListener(
                    view -> apiClient.declineInvitation(String.valueOf(item.getItem().getId()), profile.getAccessToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    invitation -> getData(),
                                    error -> System.out.println("error = " + error)
                            )
            );

            if (item.getItem().getStatusId() == 0) {
                holder.mFlipper.setDisplayedChild(1);
            }

            if (item.getItem().getStatusId() == 1) {
                holder.mFlipper.setDisplayedChild(2);
            }

            DateTimeFormatter dtf = DateTimeFormat
                    .forPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(DateTimeZone.UTC);

            // TODO: remove duplicated code, did not want to mess with updatedAt and createdAt in hurry, should be 2 min fix, nothing is wrong but it is duplicated
            DateTime createdAt;

            if (!TextUtils.isEmpty(mData.get(position).getItem().getUpdatedAt())) {
                createdAt = dtf.parseDateTime(mData.get(position).getItem().getUpdatedAt());
            } else
                createdAt = dtf.parseDateTime(mData.get(position).getItem().getCreatedAt());


            DateTime now = DateTime.now().withZone(DateTimeZone.UTC);
            String timestampHour = String.valueOf((int) ((((now.getMillis() - createdAt.getMillis()) / 1000) / 60) / 60));
            String timestampMinute = String.valueOf((((now.getMillis() - createdAt.getMillis()) / 1000) / 60) % 60);

            if (Integer.parseInt(timestampHour) == 0) {
                String text;
                if (Integer.parseInt(timestampMinute) == 1) {
                    text = timestampMinute + " minute ago";
                } else text = timestampMinute + " minutes ago";
                holder.mWhen.setText(text);
            } else {
                if (Integer.parseInt(timestampHour) > 24) {
                    int days = Integer.parseInt(timestampHour) / 24;
                    int hours = Integer.parseInt(timestampHour) - (days * 24);

                    String timestampDayHour;
                    if (days > 1) {
                        timestampDayHour = days + " days " + hours + " hr " + timestampMinute + " min ago";
                    } else
                        timestampDayHour = days + " day " + hours + " hr " + timestampMinute + " min ago";
                    holder.mWhen.setText(timestampDayHour);
                } else
                    holder.mWhen.setText(timestampHour + " hr " + timestampMinute + " min ago");
            }
        }

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(holder.itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(position);
        holder.itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override public int getItemCount() {
        return mData.size();
    }
}
