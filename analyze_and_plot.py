import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('results/results.csv')

# 1. In bảng tổng hợp giá trị CUTOFF tốt nhất cho từng bộ dữ liệu
print("=== BANG TONG HOP CUTOFF TOI UU ===")
summary = []
for ds, group in df.groupby('Dataset'):
    best_row = group.loc[group['Mean_ms'].idxmin()]
    base_row = group[group['Cutoff'] == 1].iloc[0]
    speedup = (base_row['Mean_ms'] - best_row['Mean_ms']) / base_row['Mean_ms'] * 100
    summary.append({
        'Dataset': ds,
        'Base_Cutoff1_ms': round(base_row['Mean_ms'], 2),
        'Best_Cutoff': int(best_row['Cutoff']),
        'Best_Mean_ms': round(best_row['Mean_ms'], 2),
        'Speedup_%': round(speedup, 2)
    })

summary_df = pd.DataFrame(summary)
print(summary_df.to_markdown(index=False))

# 2. Vẽ đồ thị với thanh sai số (Error bars)
plt.figure(figsize=(11, 6))
for ds, group in df.groupby('Dataset'):
    plt.errorbar(
        group['Cutoff'],
        group['Mean_ms'],
        yerr=group['StdDev_ms'],
        marker='o',
        capsize=4,
        linewidth=1.8,
        label=ds
    )

# Đánh dấu 2 mốc giáo trình (7 và 10)
plt.axvline(x=7, color='gray', linestyle=':', alpha=0.8, label='Giáo trình cũ (CUTOFF=7)')
plt.axvline(x=10, color='black', linestyle='--', alpha=0.8, label='Giáo trình mới (CUTOFF=10)')

plt.title('Ảnh hưởng của tham số CUTOFF đến thời gian chạy Mergesort (N = 1,000,000)')
plt.xlabel('Giá trị CUTOFF (Ngưỡng chuyển sang Insertion Sort)')
plt.ylabel('Thời gian chạy trung bình (ms)')
plt.xticks(df['Cutoff'].unique())
plt.grid(True, linestyle='--', alpha=0.5)
plt.legend()
plt.tight_layout()
plt.savefig('results/cutoff_chart.png', dpi=300)
plt.show()