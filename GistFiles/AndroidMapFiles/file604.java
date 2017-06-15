// While VFL-based constraint creation is a lot easier, it's also possible to create constraints by hand
// This would require more work for the same view, but allows for some cases that VFL doesn't support (like a fixed aspect ratio)
LayoutConstraint[] constraints = new LayoutConstraint[] {
    // Top
    LayoutConstraint.create(greenBox, LayoutAttribute.Top, LayoutRelation.Equal, layout,
        LayoutAttribute.Top, 1, 0),
    // greenBox.top = layout.top * 1 + 0
    LayoutConstraint.create(yellowBox, Top, Equal, layout, Top, 1, 0), // By statically importing the enum values, you get a more concise method call.

    // Left
    LayoutConstraint.create(greenBox, LayoutAttribute.Left, LayoutRelation.Equal, layout,
        LayoutAttribute.Left, 1, 0),
    LayoutConstraint.create(purpleBox, LayoutAttribute.Left, LayoutRelation.Equal, layout,
        LayoutAttribute.Left, 1, 0),

    // Right
    LayoutConstraint.create(yellowBox, LayoutAttribute.Right, LayoutRelation.Equal, layout,
        LayoutAttribute.Right, 1, 0),
    LayoutConstraint.create(purpleBox, LayoutAttribute.Right, LayoutRelation.Equal, layout,
        LayoutAttribute.Right, 1, 0),

    // Bottom
    LayoutConstraint.create(purpleBox, LayoutAttribute.Bottom, LayoutRelation.Equal, layout,
        LayoutAttribute.Bottom, 1, 0),

    // Size
    LayoutConstraint.create(greenBox, LayoutAttribute.Height, LayoutRelation.Equal, yellowBox,
        LayoutAttribute.Height, 1, 0),
    LayoutConstraint.create(greenBox, LayoutAttribute.Height, LayoutRelation.Equal, purpleBox,
        LayoutAttribute.Height, 1, 0),
    LayoutConstraint.create(greenBox, LayoutAttribute.Top, LayoutRelation.Equal, layout,
        LayoutAttribute.Top, 1, 0),

    // Spacing
    LayoutConstraint.create(yellowBox, LayoutAttribute.Left, LayoutRelation.Equal, greenBox,
        LayoutAttribute.Right, 1, 8),
    LayoutConstraint.create(purpleBox, LayoutAttribute.Top, LayoutRelation.Equal, greenBox,
        LayoutAttribute.Bottom, 1, 8),
    LayoutConstraint.create(purpleBox, LayoutAttribute.Top, LayoutRelation.Equal, yellowBox,
        LayoutAttribute.Bottom, 1, 8),
};
layout.addConstraints(constraints);