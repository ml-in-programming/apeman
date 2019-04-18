import matplotlib.pyplot as plt
import numpy as np
import matplotlib

# comparison of GEMS, JExtract, SEMI, JDeodorant precision

# font = {'family': 'normal',
#         'weight': 'normal',
#         'size': 27}
# matplotlib.rc('font', **font)
#
# color1 = (0.3,0.1,0.4,0.6)
# color2 = (0.3,0.5,0.4,0.6)
# color3 = (0.3,0.9,0.4,0.6)
#
# fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(21, 9))
#
# bar_width = 0.27
# index = np.arange(4)
# precision = list(zip(*[[22.5, 28.5, 34.3], [12.6, 13.1, 15.0], [12.9, 14.6, 18.8], [17.4, 21.1, 28.0]]))
#
# labels = ['1% tol', '2% tol', '3% tol']
#
# ax1.bar(index, precision[0], bar_width, color=color1, label=labels[0])
# ax1.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
# ax1.bar(index+2*bar_width, precision[2], bar_width, color=color3, label=labels[2])
#
# ax1.set_title("Все методы")
# ax1.set_ylabel("Точность, %", fontsize=30)
# ax1.set_xticks(index + bar_width)
# ax1.set_xticklabels(["GEMS", "JExtract", "SEMI", "JDeodorant"])
# ax1.legend()
#
# precision = list(zip(*[[13.3, 17.4, 25.3], [6.6, 8.0, 8.0], [16.4, 17.9, 19.1], [12.0, 14.3, 16.0]]))
#
# ax2.bar(index, precision[0], bar_width, color=color1, label=labels[0])
# ax2.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
# ax2.bar(index + 2*bar_width, precision[2], bar_width, color=color3, label=labels[2])
#
# ax2.set_title("Только длинные методы")
# ax2.set_ylabel("Точность, %")
# ax2.set_xticks(index + bar_width)
# ax2.set_xticklabels(["GEMS", "JExtract", "SEMI", "JDeodorant"])
# ax2.legend()
# plt.show()
# plt.savefig("all_methods_precision.png")
# print('showed')
#
#
# font = {'family': 'normal',
#         'weight': 'normal',
#         'size': 27}
# matplotlib.rc('font', **font)
#
# color1 = (0.3,0.1,0.4,0.6)
# color2 = (0.3,0.5,0.4,0.6)
# color3 = (0.3,0.9,0.4,0.6)
#
# fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(21, 9))
#
# bar_width = 0.27
# index = np.arange(3)
# precision = list(zip(*[[18.4, 23.1, 27.2], [22.5, 28.5, 34.3], [12.9, 14.6, 18.8]]))
#
# labels = ['1% tol', '2% tol', '3% tol']
#
# ax1.bar(index, precision[0], bar_width, color=color1, label=labels[0])
# ax1.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
# ax1.bar(index+2*bar_width, precision[2], bar_width, color=color3, label=labels[2])
#
# ax1.set_title("Все методы")
# ax1.set_ylabel("Точность, %", fontsize=30)
# ax1.set_xticks(index + bar_width)
# ax1.set_xticklabels(["Apeman", "GEMS",  "SEMI"])
# ax1.legend()
#
# precision = list(zip(*[[15.2, 17.2, 23.4], [13.3, 17.4, 25.3], [16.4, 17.9, 19.1]]))
#
# ax2.bar(index, precision[0], bar_width, color=color1, label=labels[0])
# ax2.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
# ax2.bar(index + 2*bar_width, precision[2], bar_width, color=color3, label=labels[2])
#
# ax2.set_title("Только длинные методы")
# # ax2.set_ylabel("Точность, %")
# ax2.set_xticks(index + bar_width)
# ax2.set_xticklabels(["Apeman", "GEMS", "SEMI"])
# ax2.legend()
# plt.show()
# plt.savefig("all_methods_precision.png")
# print('showed')


font = {'family': 'normal',
        'weight': 'normal',
        'size': 27}
matplotlib.rc('font', **font)

color1 = (0.3,0.1,0.4,0.6)
color2 = (0.3,0.5,0.4,0.6)
color3 = (0.3,0.9,0.4,0.6)

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(21, 9))

bar_width = 0.27
index = np.arange(3)
precision = list(zip(*[[50.3, 56.5, 59.8], [54.2, 59.8, 62.6], [38.0, 47.0, 55.5]]))

labels = ['1% tol', '2% tol', '3% tol']

ax1.bar(index, precision[0], bar_width, color=color1, label=labels[0])
ax1.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
ax1.bar(index+2*bar_width, precision[2], bar_width, color=color3, label=labels[2])

ax1.set_title("Все методы")
ax1.set_ylabel("Полнота, %", fontsize=30)
ax1.set_xticks(index + bar_width)
ax1.set_xticklabels(["Apeman", "GEMS",  "SEMI"])
ax1.legend()

precision = list(zip(*[[35.2, 39.3, 43.2], [31.9, 41.5, 46.2], [38.7, 41.9, 45.1]]))

ax2.bar(index, precision[0], bar_width, color=color1, label=labels[0])
ax2.bar(index+bar_width, precision[1], bar_width, color=color2, label=labels[1])
ax2.bar(index + 2*bar_width, precision[2], bar_width, color=color3, label=labels[2])

ax2.set_title("Только длинные методы")
# ax2.set_ylabel("Точность, %")
ax2.set_xticks(index + bar_width)
ax2.set_xticklabels(["Apeman", "GEMS", "SEMI"])
ax2.legend()
plt.show()
plt.savefig("all_methods_precision.png")
print('showed')
