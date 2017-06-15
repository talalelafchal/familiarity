// The easiest way of working with constraints is by using Apple's VFL (Visual Format Language).
// Documentation can be found on https://developer.apple.com/library/ios/documentation/UserExperience/Conceptual/AutolayoutPG/VisualFormatLanguage/VisualFormatLanguage.html

HashMap<String, View> viewNames = new HashMap<>();
viewNames.put("greenBox", greenBox);
viewNames.put("yellowBox", yellowBox);
viewNames.put("purpleBox", purpleBox);

HashMap<String, Dimension> metrics = new HashMap<>();
metrics.put("gutter", Dimension.sized(8, Unit.DP));
metrics.put("bodyFontSize", Dimension.sized(16, Unit.SP)); // Other metrics are provided as API sample
metrics.put("pixelSize", Dimension.sized(1, Unit.PX));
metrics.put("actionBarHeight", Dimension.getAttributeResource(android.R.attr.actionBarSize));
metrics.put("thumbnailWidth", Dimension.getDimensionResource(android.R.dimen.thumbnail_width));

String[] visualFormatLanguageStrings = new String[] {
    // Constraints are horizontal by default
    "|[greenBox(==yellowBox)]-gutter-[yellowBox]|",
    "|[purpleBox]|",

    // Vertical constraints need to have their orientation explicitly specified
    "V:|[greenBox(==purpleBox)]-gutter-[purpleBox]|",
    "V:|[yellowBox(==purpleBox)]-gutter-[purpleBox]|"
};

for (String constraintFormat : visualFormatLanguageStrings) {
  LayoutConstraint[] constraints =
      LayoutConstraint.fromVisualFormat(constraintFormat, metrics, viewNames);
  layout.addConstraints(constraints);
}
