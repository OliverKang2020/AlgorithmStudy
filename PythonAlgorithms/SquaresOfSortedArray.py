# Squared of a Sorted Array
# Given an integer array nums sorted in non-decreasing order,
# return an array of the squares of each number sorted in non-decreasing order.


class Solution(object):

    @staticmethod
    def sorted_squares(nums):
        """
        :type nums: List[int]
        :rtype: List[int]
        """

        def _squared_of_number(num):
            return num ** 2

        # map(function, iterable)
        # immutable built-in function.
        powered_nums = list(map(_squared_of_number, nums))

        # powered_nums.sort()  # non-decreasing (increasing)
        powered_nums.sort(reverse=True)  # decreasing

        return powered_nums


input_list = [-4, -1, 0, 3, 10]
solution = Solution()
print(Solution.sorted_squares(input_list))
