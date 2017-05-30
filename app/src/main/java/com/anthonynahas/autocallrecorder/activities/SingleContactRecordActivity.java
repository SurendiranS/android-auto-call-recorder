package com.anthonynahas.autocallrecorder.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Resources;
import com.anthonynahas.autocallrecorder.fragments.RecordsListFragment;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.decorators.ActionBarDecorator;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;

public class SingleContactRecordActivity extends AppCompatActivity {

    private Record mRecord;
    private RecordsListFragment mRecordsListFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_all:
                    mRecordsListFragment.refresh(createArguments(RecordDbContract.RecordItem.COLUMN_NUMBER
                                    + " = ?",
                            new String[]{mRecord.getNumber()}));
                    return true;
                case R.id.navigation_only_incoming:
                    mRecordsListFragment
                            .refresh(createArguments(RecordDbContract.RecordItem.COLUMN_NUMBER
                                            + " = ? AND "
                                            + RecordDbContract.RecordItem.RecordItem.COLUMN_IS_INCOMING
                                            + " = ?",
                                    new String[]{mRecord.getNumber(), "1"}));
                    return true;
                case R.id.navigation_only_outgoing:
                    mRecordsListFragment
                            .refresh(createArguments(RecordDbContract.RecordItem.COLUMN_NUMBER
                                            + " = ? AND "
                                            + RecordDbContract.RecordItem.RecordItem.COLUMN_IS_INCOMING
                                            + " = ?",
                                    new String[]{mRecord.getNumber(), "0"}));
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact_record);

        //Setup material action bar
        ActionBarDecorator actionBarDecorator = new ActionBarDecorator();
        actionBarDecorator.setup(this);
        actionBarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fragment instanceof RecordsListFragment) {
            mRecordsListFragment = (RecordsListFragment) fragment;
            mRecord = getIntent().getParcelableExtra(Resources.REC_PARC_KEY);

            mRecordsListFragment.refresh(createArguments(RecordDbContract.RecordItem.COLUMN_NUMBER
                            + " = ?",
                    new String[]{mRecord.getNumber()}));
        }

        if (mRecord != null) {
            // TODO: 24.05.2017 getContactName should be done asynchronously  - with async ?
            mRecord.setName(ContactHelper.getContactName(getContentResolver(), mRecord.getNumber()));
        }

        actionBarDecorator
                .getActionBar()
                .setTitle((mRecord.getName() != null ? mRecord.getName() : mRecord.getNumber()));

        if (mRecord.getName() != null) {
            actionBarDecorator
                    .getActionBar()
                    .setSubtitle(mRecord.getNumber());
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * E.G: < button: finishes the current activity
     *
     * @param item - item in the toolbar
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Bundle createArguments(String selection, String[] selectionArgs) {
        Bundle args = new Bundle();
        args.putString(RecordsListFragment.BundleArgs.selection.name(), selection);
        args.putStringArray(RecordsListFragment.BundleArgs.selectionArgs.name(), selectionArgs);

        return args;
    }

}