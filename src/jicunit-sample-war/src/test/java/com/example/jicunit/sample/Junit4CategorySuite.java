package com.example.jicunit.sample;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Categories.class)
@IncludeCategory(PositiveTestCategory.class)
@ExcludeCategory(NegativeTestCategory.class)
@SuiteClasses( { TestSampleJunit4Category.class }) // Note that Categories is a kind of Suite
public class Junit4CategorySuite {
}

