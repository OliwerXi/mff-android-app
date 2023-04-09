package com.github.oliwersdk.mff.ui.authenticated.fragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static com.github.oliwersdk.mff.SharedConstants.GLOBAL_SETTINGS;
import static com.github.oliwersdk.mff.util.Action.modify;
import static com.github.oliwersdk.mff.util.Storage.extractFileInfo;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.lang.LanguageProps;
import com.github.oliwersdk.mff.network.ServerAPI;
import com.github.oliwersdk.mff.task.AsyncImageLoader;
import com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.Page;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

// TODO: ability to change display name
public final class SettingsFragment extends AuthorizedFragment {
  private static final short FILE_PICKER_REQ_PERMISSION = 1000;
  private static final short AVATAR_PICKER_CODE         = 2000;

  private Consumer<Page> togglePage;
  private ImageView userAvatar;
  private TextView userDisplayName;
  private TextView userRank;
  private Button languageButton;

  @Override
  protected void init(View view) {
    final var user = user();
    this.userAvatar = modify((ImageView) view.findViewById(R.id.settingsUserAvatar), v -> {
      v.setOnClickListener(ignored -> {
        final var permissionName = readPermission();
        if (ActivityCompat.checkSelfPermission(getContext(), permissionName) != PERMISSION_GRANTED) {
          requestPermissions(new String[]{ permissionName }, FILE_PICKER_REQ_PERMISSION);
          return;
        }
        browseAvatar();
      });

      // image related
      v.setClipToOutline(true);
      AsyncImageLoader.load(v, user.getAvatarLink());
    });
    this.userDisplayName = modify(
      (TextView) view.findViewById(R.id.settingsUserDisplayName),
      v -> v.setText(user.displayOrUserName())
    );
    this.userRank = view.findViewById(R.id.settingsUserRank); // TODO
    this.languageButton = modify(
      (Button) view.findViewById(R.id.settingsLanguageButton),
      v -> v.setOnClickListener(ignored -> togglePage.accept(Page.LANGUAGE))
    );
  }

  @Override
  protected int layoutId() {
    return R.layout.settings_view;
  }

  @Override
  public Page page() {
    return Page.SETTINGS;
  }

  @Override
  public void update(LanguageProps props) {
    languageButton.setText(props.settingsLanguageButtonText());
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // ignore if it's not our request code or the permission was denied
    if (requestCode != FILE_PICKER_REQ_PERMISSION || grantResults.length == 0 || grantResults[0] != PERMISSION_GRANTED)
      return;

    browseAvatar();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == AVATAR_PICKER_CODE && resultCode == RESULT_OK && data != null) {
      final var fileInfo = extractFileInfo(getContext().getContentResolver(), data.getData());
      supplyAsync(() -> {
        final var fileUri = data.getData();
        try (
          final var input = getContext()
            .getContentResolver()
            .openInputStream(fileUri);
          final var output = new ByteArrayOutputStream()
        ) {
          int read;
          final var buffer = new byte[input.available()];

          while ((read = input.read(buffer, 0, buffer.length)) != -1)
            output.write(buffer, 0, read);
          output.flush();

          return ServerAPI.requestAvatarChange(
            authToken(),
            getImageMediaType(fileInfo.first),
            output.toByteArray()
          );
        } catch (Exception ex) {
          ex.printStackTrace();
          throw new RuntimeException(ex);
        }
      }).thenAccept(result -> {
        if (!result)
          return;

        final var user = user();
        if (!user.hasAvatar())
          user.setHasAvatar(true);

        AsyncImageLoader
          .load(userAvatar, user.getAvatarLink());
      });
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void setPageToggle(Consumer<Page> handler) {
    if (handler == null || togglePage != null)
      return;
    this.togglePage = handler;
  }

  private void browseAvatar() {
    startActivityForResult(
      Intent.createChooser(
        modify(new Intent(Intent.ACTION_GET_CONTENT), i -> {
          i.setType("image/*");
          i.addCategory(Intent.CATEGORY_OPENABLE);
        }),
        GLOBAL_SETTINGS
          .language()
          .chooseANewAvatarTitle()
      ),
      AVATAR_PICKER_CODE
    );
  }

  private static String readPermission() {
    return SDK_INT < VERSION_CODES.TIRAMISU ? READ_EXTERNAL_STORAGE : READ_MEDIA_IMAGES;
  }

  private static String getImageMediaType(String fileName) {
    final var extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    switch (extension) {
      case "jpg":
      case "jpeg":
      case "jpe":
      case "jif":
      case "jfif":
        return "jpeg";
      default:
        return extension;
    }
  }
}