/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.mail.ui;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.mail.R;
import com.android.mail.providers.Account;
import com.android.mail.providers.Conversation;
import com.android.mail.ui.SwipeableListView.SwipeCompleteListener;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;

public class LeaveBehindItem extends RelativeLayout implements OnClickListener, SwipeableItemView {

    private UndoOperation mUndoOp;
    private Account mAccount;
    private AnimatedAdapter mAdapter;
    private Conversation mConversation;

    public LeaveBehindItem(Context context) {
        this(context, null);
    }

    public LeaveBehindItem(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LeaveBehindItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.undo_button:
                if (mAccount.undoUri != null) {
                    // NOTE: We might want undo to return the messages affected,
                    // in which case
                    // the resulting cursor might be interesting...
                    // TODO: Use UIProvider.SEQUENCE_QUERY_PARAMETER to indicate
                    // the set of
                    // commands to undo
                    mAdapter.clearLeaveBehind(mConversation);
                    mAdapter.setUndo(true);
                    Conversation.undo(getContext(), mAccount.undoUri);
                }
                break;
        }
    }

    public void bindOperations(Account account, AnimatedAdapter adapter, UndoOperation undoOp,
            Conversation target) {
        mUndoOp = undoOp;
        mAccount = account;
        mAdapter = adapter;
        mConversation = target;
        ((TextView) findViewById(R.id.undo_description)).setText(Html.fromHtml(mUndoOp
                .getDescription(getContext())));
        ((Button) findViewById(R.id.undo_button)).setOnClickListener(this);
    }

    public void commit() {
        Conversation.delete(getContext(), ImmutableList.of(mConversation));
        mAdapter.clearLeaveBehind(mConversation);
    }

    public boolean canSwipe() {
        return true;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void cancelTap() {
        // Do nothing.
    }
}
