package com.github.oliwersdk.mff.ui.authenticated.fragment;

import static android.view.LayoutInflater.from;
import static com.github.oliwersdk.mff.R.id.languageListName;
import static com.github.oliwersdk.mff.R.layout.language_list_item;
import static com.github.oliwersdk.mff.SharedConstants.GLOBAL_SETTINGS;
import static com.github.oliwersdk.mff.lang.Language.VALUES;
import static com.github.oliwersdk.mff.lang.Language.comparator;
import static com.github.oliwersdk.mff.util.Action.modify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.oliwersdk.mff.R;
import com.github.oliwersdk.mff.lang.Language;
import com.github.oliwersdk.mff.lang.LanguageProps;
import com.github.oliwersdk.mff.ui.authenticated.AuthenticatedActivity.Page;

import java.util.LinkedList;
import java.util.List;

public final class LanguageFragment extends AuthorizedFragment {
  private Runnable goBackCaller;
  private ArrayAdapter<Language> adapter;
  private TextView selectLanguageText;

  @Override
  protected void init(View view) {
    this.selectLanguageText = view.findViewById(R.id.selectLanguageText);

    final var values = new LinkedList<>(VALUES);
    values.sort(comparator().reversed());

    final var languageList = (ListView) view.findViewById(R.id.languageListView);
    languageList.setAdapter((this.adapter = new Adapter(getContext(), values)));
    languageList.setOnItemClickListener((ignored, itemView, index, l) ->
      GLOBAL_SETTINGS.setLanguage(values.get(index))
    );

    view
      .findViewById(R.id.languageBackIcon)
      .setOnClickListener(ignored -> {
        if (goBackCaller != null)
          goBackCaller.run();
      });
  }

  @Override
  protected int layoutId() {
    return R.layout.language_fragment;
  }

  @Override
  public Page page() {
    return Page.LANGUAGE;
  }

  @Override
  public void update(LanguageProps props) {
    selectLanguageText.setText(props.selectNewLanguageTitle());
    ((Language.RefreshableComponent) this.adapter).update(GLOBAL_SETTINGS.language());
  }

  public void setGoBackCaller(Runnable runnable) {
    if (runnable == null || goBackCaller != null)
      return;
    this.goBackCaller = runnable;
  }

  public static final class Adapter extends ArrayAdapter<Language>
    implements Language.RefreshableComponent {
    private Adapter(@NonNull Context context, @NonNull List<Language> objects) {
      super(context, language_list_item, languageListName, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      if (convertView == null)
        convertView = from(getContext()).inflate(language_list_item, parent, false);

      final var language = getItem(position);
      assert language != null;

      final var props = GLOBAL_SETTINGS.language();
      modify(
        (TextView) convertView.findViewById(languageListName),
        v -> v.setText(props.languageName(language))
      );
      return convertView;
    }

    @Override
    public void update(LanguageProps props) {
      notifyDataSetChanged(); // refresh present views
    }
  }
}