import matplotlib.pyplot as plt
import numpy as np
import matplotlib

# comparison of GEMS, JExtract, SEMI, JDeodorant precision

font = {'family' : 'normal',
        'weight' : 'normal',
        'size'   : 16}

matplotlib.rc('font', **font)

# fig, ax = plt.subplots()
#
# bar_width = 0.27
# index = np.arange(4)
# precision = list(zip(*[[22.5, 28.5, 34.3], [12.6, 13.1, 15.0], [12.9, 14.6, 18.8], [17.4, 21.1, 28.0]]))
#
# labels = ['1% tol', '2% tol', '3% tol']
#
# ax.bar(index, precision[0], bar_width, color='b', label=labels[0])
# ax.bar(index+bar_width, precision[1], bar_width, color='r', label=labels[1])
# ax.bar(index+2*bar_width, precision[2], bar_width, color='g', label=labels[2])
#
# ax.set_title("Точность решений для всех методов")
# ax.set_ylabel("Точность, %")
# ax.set_xticks(index + bar_width)
# ax.set_xticklabels(["GEMS", "JExtract", "SEMI", "JDeodorant"])
# ax.legend()
# plt.show()
# plt.savefig("all_methods_precision.jpg")
# print('showed')


# 2

font = {'family' : 'normal',
        'weight' : 'normal',
        'size'   : 16}

fig, ax = plt.subplots()

bar_width = 0.27
index = np.arange(4)
precision = list(zip(*[[13.3, 17.4, 25.3], [6.6, 8.0, 8.0], [16.4, 17.9, 19.1], [12.0, 14.3, 16.0]]))

labels = ['1% tol', '2% tol', '3% tol']

ax.bar(index, precision[0], bar_width, color='b', label=labels[0])
ax.bar(index+bar_width, precision[1], bar_width, color='r', label=labels[1])
ax.bar(index + 2*bar_width, precision[2], bar_width, color='g', label=labels[2])

ax.set_title("Точность решений для длинных методов")
ax.set_ylabel("Точность, %")
ax.set_xticks(index + bar_width)
ax.set_xticklabels(["GEMS", "JExtract", "SEMI", "JDeodorant"])
ax.legend()
plt.show()
plt.savefig("long_methods_precision.jpg")
print('showed')
