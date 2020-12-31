package com.zpf.support.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.frame.IRootLayout;
import com.zpf.frame.ITitleBar;
import com.zpf.support.R;
import com.zpf.tool.ViewUtil;

import java.util.LinkedList;

public class ContainerRootLayout extends ViewGroup implements IRootLayout {
    private final LinkedList<ViewNode> contentDecorations = new LinkedList<>();
    private final LinkedList<ViewNode> pageDecorations = new LinkedList<>();
    private View defStatusBar;
    private TitleBar defTitleBar;

    private View statusBar;
    private ITitleBar titleBar;
    private View contentView;
    private LayoutParams defLayoutParams;
    private boolean contentBelowTitle = true;

    public ContainerRootLayout(Context context) {
        super(context);
        initConfig(context, null);
    }

    public ContainerRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig(context, attrs);
    }

    public ContainerRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
    }

    protected void initConfig(Context context, AttributeSet attrs) {
        defLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        super.setLayoutParams(defLayoutParams);
        defStatusBar = new View(context);
        statusBar = defStatusBar;
        super.addView(defStatusBar, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewUtil.getStatusBarHeight(context)));
        defTitleBar = new TitleBar(context);
        titleBar = defTitleBar;
        super.addView(defTitleBar);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContainerRootLayout);
        if (array != null) {
            contentBelowTitle = array.getBoolean(R.styleable.ContainerRootLayout_contentBelowTitle, true);
            array.recycle();
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    @Override
    public void onViewAdded(View child) {
        //添加到渲染列表中,不合法的视图会被加入子视图列表，但不会去渲染
        if (child != statusBar && child != contentView && child != titleBar) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.childType == ChildType.statusBar) {
                statusBar = child;
            } else if (lp.childType == ChildType.titleBar) {
                if (child instanceof ITitleBar) {
                    titleBar = (ITitleBar) child;
                }
            } else if (lp.childType == ChildType.contentView) {
                contentView = child;
            } else if (lp.childType == ChildType.contentDecoration) {
                boolean addToLast = true;
                for (int i = 0; i < contentDecorations.size(); i++) {
                    if (contentDecorations.get(i).hierarchy > lp.hierarchy) {
                        addToLast = false;
                        contentDecorations.add(i, new ViewNode(child, lp.hierarchy));
                        break;
                    }
                }
                if (addToLast) {
                    contentDecorations.add(new ViewNode(child, lp.hierarchy));
                }
            } else if (lp.childType == ChildType.pageDecoration) {
                boolean addToLast = true;
                for (int i = 0; i < pageDecorations.size(); i++) {
                    if (pageDecorations.get(i).hierarchy > lp.hierarchy) {
                        addToLast = false;
                        pageDecorations.add(i, new ViewNode(child, lp.hierarchy));
                        break;
                    }
                }
                if (addToLast) {
                    pageDecorations.add(new ViewNode(child, lp.hierarchy));
                }
            }
        }
    }

    @Override
    public void onViewRemoved(View child) {
        //从渲染列表中移除,statusBar、titleBar不会被移除，只会回退默认视图然后被隐藏
        if (child == statusBar) {
            child = defStatusBar;
            child.setVisibility(View.GONE);
        } else if (child == titleBar) {
            child = defTitleBar;
            child.setVisibility(View.GONE);
        } else if (child == contentView) {
            contentView = null;
        } else {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.childType == ChildType.contentDecoration) {
                for (int i = 0; i < contentDecorations.size(); i++) {
                    if (contentDecorations.get(i).view == child) {
                        contentDecorations.remove(i);
                        break;
                    }
                }
            } else if (lp.childType == ChildType.pageDecoration) {
                for (int i = 0; i < pageDecorations.size(); i++) {
                    if (pageDecorations.get(i).view == child) {
                        pageDecorations.remove(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingHorizontal = getPaddingLeft() + getPaddingRight();
        int paddingVertical = getPaddingTop() + getPaddingBottom();
        int statusBarHeight = measureChild(statusBar, widthMeasureSpec, paddingHorizontal,
                heightMeasureSpec, paddingVertical);
        final View titleBarView;
        if (titleBar != null) {
            titleBarView = titleBar.getLayout();
        } else {
            titleBarView = null;
        }
        int titleBarHeight = measureChild(titleBarView, widthMeasureSpec, paddingHorizontal,
                heightMeasureSpec, paddingVertical);
        if (contentView != null && contentView.getVisibility() != View.GONE) {
            int userHeight;
            if (contentBelowTitle) {
                userHeight = paddingVertical + statusBarHeight + titleBarHeight;
            } else {
                userHeight = paddingVertical;
            }
            measureChild(contentView, widthMeasureSpec, paddingHorizontal,
                    heightMeasureSpec, userHeight);
            for (ViewNode node : contentDecorations) {
                measureChild(node.view, widthMeasureSpec, paddingHorizontal,
                        heightMeasureSpec, userHeight);
            }
        }
        for (ViewNode node : pageDecorations) {
            measureChild(node.view, widthMeasureSpec, paddingHorizontal,
                    heightMeasureSpec, paddingVertical);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int direction = getLayoutDirection();
        final int realLeft = l + getPaddingStart();
        final int realTop = t + getPaddingTop();
        final int realRight = r - getPaddingEnd();
        final int realBottom = b - getPaddingBottom();
        int statusBarHeight = layoutChild(statusBar, realLeft, realTop, realRight, realBottom, direction);
        final View titleBarView;
        if (titleBar != null) {
            titleBarView = titleBar.getLayout();
        } else {
            titleBarView = null;
        }
        int titleBarHeight = layoutChild(titleBarView, realLeft, realTop + statusBarHeight, realRight, realBottom, direction);
        if (contentView != null && contentView.getVisibility() != View.GONE) {
            int contentStatTop;
            if (contentBelowTitle) {
                contentStatTop = realTop + statusBarHeight + titleBarHeight;
            } else {
                contentStatTop = realTop;
            }
            layoutChild(contentView, realLeft, contentStatTop, realRight, realBottom, direction);
            for (ViewNode node : contentDecorations) {
                layoutChild(node.view, realLeft, contentStatTop, realRight, realBottom, direction);
            }
        }
        for (ViewNode node : pageDecorations) {
            layoutChild(node.view, realLeft, realTop, realRight, realBottom, direction);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final boolean drawContentBelowTitle = contentBelowTitle;
        final long drawTime = getDrawingTime();
        if (!drawContentBelowTitle) {
            if (contentView != null && contentView.getVisibility() != View.GONE) {
                doDrawChild(canvas, contentView, drawTime);
                for (ViewNode node : contentDecorations) {
                    doDrawChild(canvas, node.view, drawTime);
                }
            }
        }
        drawChild(canvas, statusBar, drawTime);
        if (titleBar != null) {
            doDrawChild(canvas, titleBar.getLayout(), drawTime);
        }
        if (drawContentBelowTitle) {
            if (contentView != null && contentView.getVisibility() != View.GONE) {
                doDrawChild(canvas, contentView, drawTime);
                for (ViewNode node : contentDecorations) {
                    doDrawChild(canvas, node.view, drawTime);
                }
            }
        }
        for (ViewNode node : pageDecorations) {
            doDrawChild(canvas, node.view, drawTime);
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (params != defLayoutParams) {
            if (params instanceof LayoutParams) {
                defLayoutParams = (LayoutParams) params;
            } else {
                defLayoutParams = new LayoutParams(params);
            }
            if (defLayoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                defLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (defLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                defLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        super.setLayoutParams(params);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return (LayoutParams) p;
        }
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        if (defLayoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            defLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if (defLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            defLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return super.getLayoutParams();
    }

    @NonNull
    @Override
    public View getStatusBar() {
        return statusBar;
    }

    @NonNull
    @Override
    public ITitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    public void changeTitleBar(@NonNull ITitleBar titleBar) {
        this.titleBar = titleBar;
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Nullable
    @Override
    public View getContentView() {
        return contentView;
    }

    public void setContentBelowTitle(boolean contentBelowTitle) {
        if (this.contentBelowTitle != contentBelowTitle) {
            this.contentBelowTitle = contentBelowTitle;
            requestLayout();
        }
    }

    @Override
    public void setContentView(int viewResId) {
        View view = LayoutInflater.from(getContext()).inflate(viewResId, this, false);
        setContentView(view);
    }

    @Override
    public void setContentView(@NonNull View view) {
        if (contentView != null) {
            removeView(contentView);
        }
        addView(view);
        contentView = view;
    }

    @Override
    public void addPageDecoration(@NonNull View child, int hierarchy, @Nullable ViewGroup.LayoutParams params) {
        if (child == null || child.getParent() != null) {
            return;
        }
        LayoutParams lp;
        if (params instanceof LayoutParams) {
            lp = ((LayoutParams) params);
        } else if (params == null) {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new LayoutParams(params);
        }
        lp.hierarchy = hierarchy;
        lp.childType = ChildType.pageDecoration;
        super.addView(child, lp);
    }

    @Override
    public void addContentDecoration(@NonNull View child, int hierarchy, @Nullable ViewGroup.LayoutParams params) {
        if (child == null || child.getParent() != null) {
            return;
        }
        LayoutParams lp;
        if (params instanceof LayoutParams) {
            lp = ((LayoutParams) params);
        } else if (params == null) {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new LayoutParams(params);
        }
        lp.hierarchy = hierarchy;
        lp.childType = ChildType.contentDecoration;
        super.addView(child, lp);
    }

    public @interface ChildType {
        int statusBar = 0;
        int titleBar = 1;
        int contentView = 2;
        int contentDecoration = 3;
        int pageDecoration = 4;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public int hierarchy;
        public int childType = -1;

        @SuppressLint("CustomViewStyleable")
        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ContainerRootLayout);
            if (a != null) {
                hierarchy = a.getInt(R.styleable.ContainerRootLayout_hierarchy, 0);
                childType = a.getInt(R.styleable.ContainerRootLayout_childType, -1);
                a.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof FrameLayout.LayoutParams) {
                this.gravity = ((FrameLayout.LayoutParams) source).gravity;
            }
        }
    }

    private int measureChild(View child, int parentWidthMeasureSpec, int widthUsed,
                             int parentHeightMeasureSpec, int heightUsed) {
        if (child == null || child.getVisibility() == View.GONE) {
            return 0;
        }
        int childMarginLeft = 0;
        int childMarginRight = 0;
        int childMarginTop = 0;
        int childMarginBottom = 0;
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof LayoutParams) {
            childMarginLeft = ((LayoutParams) lp).leftMargin;
            childMarginRight = ((LayoutParams) lp).rightMargin;
            childMarginTop = ((LayoutParams) lp).topMargin;
            childMarginBottom = ((LayoutParams) lp).bottomMargin;
        }
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                childMarginLeft + childMarginRight + widthUsed, lp.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                childMarginTop + childMarginBottom + heightUsed, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        return child.getMeasuredHeight();
    }

    @SuppressLint("RtlHardcoded")
    private int layoutChild(View child, int left, int top, int right, int bottom, int layoutDirection) {
        if (child != null && child.getVisibility() != View.GONE) {
            final ViewGroup.LayoutParams lp = child.getLayoutParams();
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            int childLeft;
            int childTop;
            int childMarginLeft = 0;
            int childMarginRight = 0;
            int childMarginTop = 0;
            int childMarginBottom = 0;
            int gravity = -1;
            if (lp instanceof LayoutParams) {
                gravity = ((LayoutParams) lp).gravity;
                childMarginLeft = ((LayoutParams) lp).leftMargin;
                childMarginRight = ((LayoutParams) lp).rightMargin;
                childMarginTop = ((LayoutParams) lp).topMargin;
                childMarginBottom = ((LayoutParams) lp).bottomMargin;
            }
            if (gravity == -1) {
                gravity = Gravity.TOP | Gravity.START;
            }
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = left + (right - left - width) / 2 +
                            childMarginLeft - childMarginRight;
                    break;
                case Gravity.RIGHT:
                    childLeft = right - width - childMarginRight;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = left + childMarginLeft;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = top + (bottom - top - height) / 2 +
                            childMarginTop - childMarginBottom;
                    break;
                case Gravity.BOTTOM:
                    childTop = bottom - height - childMarginBottom;
                    break;
                default:
                    childTop = top + childMarginTop;
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
            return height + childMarginTop + childMarginBottom;
        }
        return 0;
    }

    private void doDrawChild(Canvas canvas, View child, long drawingTime) {
        if (child != null && child.getVisibility() != View.GONE) {
            drawChild(canvas, child, drawingTime);
        }
    }

    private static class ViewNode {
        View view;
        int hierarchy;

        public ViewNode(View view, int hierarchy) {
            this.view = view;
            this.hierarchy = hierarchy;
        }
    }
}
