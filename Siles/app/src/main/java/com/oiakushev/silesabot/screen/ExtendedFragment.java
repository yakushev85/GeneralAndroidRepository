package com.oiakushev.silesabot.screen;

import androidx.fragment.app.Fragment;

import com.oiakushev.silesabot.MainActivity;

public class ExtendedFragment extends Fragment {
    protected MainActivity getMainActivity() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        } else {
            throw new IllegalStateException("ExtendedFragment.getMainActivity can't access to MainActivity.");
        }
    }
}
