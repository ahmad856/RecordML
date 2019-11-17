package com.example.recordml.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.recordml.R;
import com.example.recordml.fragments.RecordingStats;
import com.example.recordml.fragments.RecordingsText;
import com.example.recordml.models.Recording;
import com.google.android.material.tabs.TabLayout;
import java.util.Objects;

public class RecordingsTab extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static int TAB_COUNT = 2;

    private Recording record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        record = (Recording) Objects.requireNonNull(getIntent().getExtras()).getSerializable(RecordingsListView.RECORDING_KEY);

        setContentView(R.layout.activity_recordings_tab);

        mViewPager = findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String[] tabNames = new String[]{"File Text", "File Stats"};
        private Context context;

        SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new RecordingsText(context, record);
            } else if (position == 1) {
                return new RecordingStats(context, record);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
