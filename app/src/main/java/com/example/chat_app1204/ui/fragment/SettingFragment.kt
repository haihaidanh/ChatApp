package com.example.chat_app1204.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chat_app1204.R
import com.example.chat_app1204.databinding.FragmentSettingBinding
//import com.giphy.sdk.core.models.Media
//import com.giphy.sdk.ui.GPHContentType
//import com.giphy.sdk.ui.GPHSettings
//import com.giphy.sdk.ui.Giphy
//import com.giphy.sdk.ui.themes.GPHTheme
//import com.giphy.sdk.ui.views.GiphyDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var mBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Giphy.configure(requireContext(), "d7iMD5LW86AvOQy4RK2JRB4nod4lMj4k")
//        mBinding.btnTest.setOnClickListener {
//            val settings = GPHSettings(
//                // 1. Chỉ định danh sách các loại nội dung cho phép (ở đây chỉ cho phép Sticker)
//                mediaTypeConfig = arrayOf(GPHContentType.sticker),
//
//                // 2. Nội dung mặc định sẽ hiển thị khi mở lên là Sticker
//                selectedContentType = GPHContentType.sticker,
//
//                // 3. Các cài đặt khác của bạn
//                theme = GPHTheme.Dark,
//                showConfirmationScreen = false,
//                stickerColumnCount = 3
//            )
//
//            // 3. Tạo DialogFragment
//            val giphyDialog = GiphyDialogFragment.newInstance(settings)
//
//            // 4. Lắng nghe sự kiện chọn Sticker
//            giphyDialog.gifSelectionListener = object : GiphyDialogFragment.GifSelectionListener {
//                override fun onGifSelected(
//                    media: Media,
//                    searchTerm: String?,
//                    selectedContentType: GPHContentType
//                ) {
//                    // Lấy URL của sticker (dạng GIF để hiển thị động)
//                    val url = media.images.fixedHeight?.gifUrl
//
//                    // 5. Hiển thị lên màn hình bằng Glide
//                    Glide.with(requireContext())
//                        .asGif()
//                        .load(url)
//                        .into(mBinding.imgTest)
//
//                    giphyDialog.dismiss() // Đóng bảng chọn
//                }
//
//                override fun onDismissed(selectedContentType: GPHContentType) {}
//                override fun didSearchTerm(term: String) {}
//            }
//
//            // 6. Hiển thị Dialog (Nó sẽ tự động trượt từ dưới lên)
//            giphyDialog.show(requireActivity().supportFragmentManager, "giphy_sticker")
//
//        }
    }
}
