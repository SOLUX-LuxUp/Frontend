package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.solux.luxup.taptap.core.ui.theme.ProfileIcon
import com.solux.luxup.taptap.feature.auth.account.presentation.components.AccountImageSourceModal
import com.solux.luxup.taptap.feature.auth.account.presentation.components.ProfileIconPickerSheet

/**
 * 아바타 톱니 클릭 → "이미지로 설정"/"아이콘으로 설정" 팝업 → (갤러리 피커 | 아이콘 하단 팝업).
 * [content] 에 [onEditProfileClick] 을 넘겨주므로, 호출부는 그 람다를 화면의 톱니 클릭에 연결하면 된다.
 */
@Composable
fun AccountProfileImageEditor(
    accountViewModel: AccountViewModel,
    content: @Composable (onEditProfileClick: () -> Unit) -> Unit,
) {
    var showSourceModal by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { accountViewModel.updateProfileImage(it.toString()) }
    }

    content { showSourceModal = true }

    if (showSourceModal) {
        AccountImageSourceModal(
            onSelectImage = {
                showSourceModal = false
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onSelectIcon = {
                showSourceModal = false
                showIconPicker = true
            },
            onDismiss = { showSourceModal = false },
        )
    }

    if (showIconPicker) {
        ProfileIconPickerSheet(
            initialIcon = ProfileIcon.parseToken(accountViewModel.profile?.profileImageUrl),
            onDismiss = { showIconPicker = false },
            onConfirm = { icon ->
                accountViewModel.updateProfileImage(ProfileIcon.toToken(icon))
                showIconPicker = false
            },
        )
    }
}